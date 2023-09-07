package com.hmis.server.hmis.modules.shift.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.service.*;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.constant.PaymentMethodEnum;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.BillPaymentOptionTypeEnum;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.billing.service.PatientPaymentServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum;
import com.hmis.server.hmis.modules.shift.dto.*;
import com.hmis.server.hmis.modules.shift.iservice.ICashierShiftService;
import com.hmis.server.hmis.modules.shift.model.CashierCompiledShift;
import com.hmis.server.hmis.modules.shift.model.CashierFundReception;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import com.hmis.server.hmis.modules.shift.repository.CashierShiftRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;
import static com.hmis.server.hmis.common.constant.PaymentMethodEnum.*;
import static com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum.DEPOSIT;
import static com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum.DRUG;

@Service
@Slf4j
public class CashierShiftServiceImpl implements ICashierShiftService {
    private final CashierShiftRepository cashierShiftRepository;
    private final CommonService commonService;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;
    private final PatientPaymentServiceImpl paymentService;
    private final CashierCompiledShiftServiceImpl compiledShiftService;
    private final CashierFundReceptionServiceImpl fundReceptionService;
    private final HmisUtilService utilService;
    private final GlobalSettingsImpl globalSettingsService;
    private final RevenueDepartmentServiceImpl revenueDepartmentService;

    List<CashierDetailedReportDto> detailedReportDtoList = new ArrayList<>();

    @Autowired
    public CashierShiftServiceImpl(
            CashierShiftRepository cashierShiftRepository,
            CommonService commonService,
            UserServiceImpl userService,
            DepartmentServiceImpl departmentService,
            PatientPaymentServiceImpl paymentService,
            CashierCompiledShiftServiceImpl compiledShiftService,
            CashierFundReceptionServiceImpl fundReceptionService,
            HmisUtilService utilService,
            GlobalSettingsImpl globalSettingsService,
            RevenueDepartmentServiceImpl revenueDepartmentService) {
        this.cashierShiftRepository = cashierShiftRepository;
        this.commonService = commonService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.paymentService = paymentService;
        this.compiledShiftService = compiledShiftService;
        this.fundReceptionService = fundReceptionService;
        this.utilService = utilService;
        this.globalSettingsService = globalSettingsService;
        this.revenueDepartmentService = revenueDepartmentService;
    }

    // get a shift or create shift if non is currently active
    public CashierShift getOrCreateShift(CashierShiftSetterDto dto) {
        Optional<CashierShift> activeShift = this.findActiveShift(dto.getCashier());
        return activeShift.orElseGet(() -> this.createNewShift(dto));
    }

    // find current cashier shift record and increment receipt count column on
    // database table
    public CashierShift findAndIncrementReceiptCount(CashierShiftSetterDto dto) {
        CashierShift shift = this.getOrCreateShift(dto);
        this.incrementReceiptCount(shift);
        return shift;
    }

    // find cashier shift record by shift number
    public CashierShift findByShiftNumber(String shiftNumber) {
        return this.cashierShiftRepository
                .findByCode(shiftNumber)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Shift Number");
                });
    }

    // increment cashier shift receipt count
    public void incrementReceiptCount(CashierShift shift) {
        int count = shift.getReceiptCount();
        shift.setReceiptCount(count + 1);
        this.cashierShiftRepository.save(shift);
    }

    // create new shift record
    public CashierShift createNewShift(CashierShiftSetterDto dto) {
        CashierShift shift = new CashierShift();
        shift.setCashier(dto.getCashier());
        shift.setDepartment(dto.getLocation());
        shift.setCode(this.generateShitCode());
        shift.setOpenDate(LocalDate.now());
        shift.setOpenTime(LocalTime.now());
        return this.cashierShiftRepository.save(shift);
    }

    // find the current active shift record, only one shift can be active per day
    public Optional<CashierShift> findActiveShift(User cashier) {
        this.closeAnyPreviousActiveShift(cashier);
        return this.cashierShiftRepository.findByCashierAndIsActiveAndOpenDate(cashier,
                true, LocalDate.now());
    }

    // close any previous cashier shift record, checks if date is past today
    public void closeAnyPreviousActiveShift(User cashier) {
        List<CashierShift> shifts = this.cashierShiftRepository.findAllByCashierAndOpenDateIsBeforeAndIsActive(cashier,
                LocalDate.now(), true);
        if (!shifts.isEmpty()) {
            for (CashierShift shift : shifts) {
                this.closeShift(shift, ShiftCloseTypeEnum.CLOSE_BY_SYSTEM, LocalDate.now(), LocalTime.now());
            }
        }
    }

    public ResponseEntity<Boolean> closeShiftByCashier(String shiftNumber, Long userId) {
        try {
            User user = this.userService.findOneRaw(userId);
            if (this.isUserShiftOwner(userId, shiftNumber)) {
                CashierShift shift = this.findByCodeOrFail(shiftNumber);
                shift.setClosedByUser(user);
                shift.setIsClosedByCashier(true);
                LocalTime closeTime = LocalTime.now();
                LocalDate closeDate = LocalDate.now();
                ShiftCloseTypeEnum closeTypeEnum = ShiftCloseTypeEnum.CLOSE_BY_USER;
                this.closeShift(shift, closeTypeEnum, closeDate, closeTime);
                return ResponseEntity.status(HttpStatus.OK).body(true);
            }
            return ResponseEntity.status(HttpStatus.OK).body(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }

    public CashierShift findOneById(Long id) {
        return this.cashierShiftRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Found"));
    }

    public void closeShiftById(Long shiftId, Long closedBy) {
        CashierShift shift = this.findOneById(shiftId);
        if (shift.getIsActive()) {
            shift.setIsActive(false);
            shift.setCloseDate(LocalDate.now());
            shift.setCloseTypeEnum(ShiftCloseTypeEnum.CLOSE_BY_USER);
            shift.setCloseTime(LocalTime.now());
            shift.setClosedByUser(this.userService.findOneRaw(closedBy));
            shift.setIsClosedByCashier(shift.getCashier().getId().equals(closedBy));
            this.cashierShiftRepository.save(shift);
        }
    }

    public ResponseEntity<ShiftCodeSearchDto> findCashierActiveCode(Long userId) {
        try {
            User user = this.userService.findOneRaw(userId);
            Optional<CashierShift> shift = this.findActiveShift(user);
            return shift
                    .map(cashierShift -> ResponseEntity.status(HttpStatus.OK)
                            .body(new ShiftCodeSearchDto(cashierShift.getCode())))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.OK).body(new ShiftCodeSearchDto()));
        } catch (Exception e) {
            log.error(e.getMessage(), "No Active Code Found For Code Lookup.");
            return ResponseEntity.badRequest().body(null);
        }
    }

    public void runClearCashierShiftRecordSchedule() {
        try {
            LocalDate openDate = LocalDate.now().minusDays(1);
            LocalTime closeTime = LocalTime.now();
            LocalDate closeDate = LocalDate.now();
            ShiftCloseTypeEnum closeTypeEnum = ShiftCloseTypeEnum.CLOSE_BY_SYSTEM;
            List<CashierShift> shifts = this.cashierShiftRepository.findAllByOpenDateAndIsActive(openDate, true);
            if (!shifts.isEmpty()) {
                for (CashierShift shift : shifts) {
                    shift.setIsClosedByCashier(false);
                    this.closeShift(shift, closeTypeEnum, closeDate, closeTime);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.println("An Error Occurred While Clearing Cashier Shift Record " + e.getMessage());
        }
    }

    public CashierShift findByCodeOrFail(String code) {
        return this.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift with code not found"));
    }

    public Optional<CashierShift> findByCode(String code) {
        return this.cashierShiftRepository.findByCode(code);
    }

    public List<ShiftCodeSearchDto> searchCashierShiftTypeAhead(String search, boolean hideClosedShift) {
        List<ShiftCodeSearchDto> list = new ArrayList<>();
        List<CashierShift> all = this.cashierShiftRepository.findAllByCodeIsContainingIgnoreCase(search);
        if (!all.isEmpty()) {
            for (CashierShift shift : all) {
                ShiftCodeSearchDto dto = new ShiftCodeSearchDto();
                dto.setId(shift.getId());
                dto.setShiftNumber(shift.getCode());
                dto.setUsername(shift.getCashier().getUserName());
                dto.setOpenDate(shift.getOpenDate().toString());
                dto.setStatus(shift.getIsActive());
                dto.setFullName(shift.getCashier().getFullName());
                if (!shift.getIsActive()) {
                    dto.setCloseDate(shift.getCloseDate().toString());
                }

                if (hideClosedShift && !shift.getIsActive()) {
                    continue;
                }

                list.add(dto);
            }
        }
        return list;
    }

    public CashierShiftWrapperDto findShiftWrapperByShiftId(long shiftId) {
        Optional<CashierShift> optionalCashierShift = this.cashierShiftRepository.findById(shiftId);
        if (!optionalCashierShift.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cashier Shift Cannot Be Found");
        }
        CashierShiftDto dto = this.mapCashierShiftModelToDto(optionalCashierShift.get());
        List<CashierShiftDto> list = Arrays.asList(dto);
        CashierShiftWrapperDto wrapperDto = new CashierShiftWrapperDto();
        this.setShiftReportAmountSumValue(list, wrapperDto);
        return wrapperDto;
    }

    public CashierShiftDto findCashierShiftByShiftId(Long id) {
        CashierShift cashierShift = this.findOneById(id);
        return this.mapCashierShiftModelToDto(cashierShift);
    }

    public CashierShiftWrapperDto findShiftWrapperByDateRange(CashierShiftSearchDto dto) {
        CashierShiftWrapperDto wrapperDto = new CashierShiftWrapperDto();
        List<CashierShiftDto> list = new ArrayList<>();
        try {
            List<CashierShift> cashierShifts = this.findCashierRecordByDateRange(dto);
            if (!cashierShifts.isEmpty()) {
                for (CashierShift shift : cashierShifts) {
                    list.add(this.mapCashierShiftModelToDto(shift));
                }
            }
            this.setShiftReportAmountSumValue(list, wrapperDto);
            return wrapperDto;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public List<CashierShiftDto> findShiftByDateRangeAndCashierRequired(CashierShiftSearchDto dto) {
        if (dto.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cashier is required");
        }
        List<CashierShiftDto> dtoList = new ArrayList<>();
        try {
            LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
            LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
            User cashier = this.userService.findOneRaw(dto.getUserId());
            List<CashierShift> list = this.cashierShiftRepository
                    .findAllByCashierAndOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(cashier, endDate,
                            startDate);
            if (!list.isEmpty()) {
                dtoList = list.stream().map(this::mapCashierShiftModelToDto).collect(Collectors.toList());
            }
            return dtoList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return dtoList;
        }
    }

    public List<CashierShiftDto> findShiftByDateRangeAndCashierNotRequired(CashierShiftSearchDto dto) {
        List<CashierShiftDto> dtoList = new ArrayList<>();
        try {
            LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
            LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
            List<CashierShift> list = new ArrayList<>();
            if (dto.getSearchBy().equals(SearchShiftByEnum.CASHIER_USERNAME)) {
                if (dto.getUserId() != null) {
                    User cashier = this.userService.findOneRaw(dto.getUserId());
                    list = this.cashierShiftRepository
                            .findAllByCashierAndOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(cashier, endDate,
                                    startDate);
                } else {
                    list = this.cashierShiftRepository
                            .findAllByOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(endDate, startDate);
                }
            } else if (dto.getSearchBy().equals(SearchShiftByEnum.SHIFT_NUMBER)) {
                if (dto.getShiftId() != null) {
                    list = new ArrayList<>(Arrays.asList(this.findOneById(dto.getShiftId())));
                } else {
                    list = this.cashierShiftRepository
                            .findAllByOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(endDate, startDate);
                }
            }

            if (!list.isEmpty()) {
                dtoList = list.stream().map(this::mapCashierShiftModelToDto).collect(Collectors.toList());
            }
            return dtoList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return dtoList;
        }
    }

    public List<CashierShiftDto> findShiftByDateRangeForReception(CashierShiftSearchDto dto) {
        List<CashierShiftDto> shifts = new ArrayList<>();
        List<CashierShift> shiftList = this.findCashierRecordByDateRange(dto);
        if (!shiftList.isEmpty()) {
            shifts = shiftList.stream().map(this::mapCashierShiftModelToDto).collect(Collectors.toList());
        }
        return shifts;
    }

    public ResponseEntity<Boolean> closeMultipleShiftRecord(CloseShiftDto dto) {
        if (!dto.getShiftsId().isEmpty()) {
            for (Long shiftId : dto.getShiftsId()) {
                this.closeShiftById(shiftId, dto.getClosedBy());
            }
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        log.error("Shift Records Is Empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    public List<CashierDetailedReportDto> getPaymentAmountByServiceDepartmentAndPaymentMethod(PatientPayment payment) {
        List<CashierDetailedReportDto> list = this.detailedReportDtoList;
        // todo:: refactor this block create separate method for each case: service,
        // drug & deposit
        // create reusable methods to remove duplicates
        PaymentTypeForEnum paymentFor = PaymentTypeForEnum.getPaymentTypeForEnum(payment.getPaymentTypeForEnum());
        String paymentMethodName = payment.getPaymentMethod().getName();
        switch (paymentFor) {
            case SERVICE:
                for (PatientServiceBillItem item : payment.getPatientBill().getPatientServiceBillItems()) {
                    Department serviceDepartment = item.getProductService().getDepartment();
                    Optional<CashierDetailedReportDto> first = list.stream()
                            .filter(o -> o.getServiceDepartmentName().equals(serviceDepartment.getName())).findFirst();
                    String itemName = item.getProductService().getName();
                    if (first.isPresent()) {
                        CashierDetailedReportDto dto = first.get();
                        Optional<CashierShiftServiceDto> service = dto.getServiceList().stream()
                                .filter(o -> o.getTitle().equals(itemName)).findFirst();
                        if (service.isPresent()) {
                            // service is present in detailed collection
                            CashierShiftServiceDto serviceDto = service.get();
                            serviceDto.setCashTotal(this.setServiceDtoAmount(serviceDto.getCashTotal(), CASH,
                                    item.getNetAmount(), paymentMethodName));
                            serviceDto.setChequeTotal(this.setServiceDtoAmount(serviceDto.getChequeTotal(),
                                    PaymentMethodEnum.CHEQUE, item.getNetAmount(), paymentMethodName));
                            serviceDto.setPosTotal(this.setServiceDtoAmount(serviceDto.getPosTotal(),
                                    PaymentMethodEnum.POS, item.getNetAmount(), paymentMethodName));
                            serviceDto.setEftTotal(this.setServiceDtoAmount(serviceDto.getEftTotal(),
                                    PaymentMethodEnum.ETF, item.getNetAmount(), paymentMethodName));
                            serviceDto.setMobileTotal(this.setServiceDtoAmount(serviceDto.getMobileTotal(),
                                    PaymentMethodEnum.MOBILE_MONEY, item.getNetAmount(), paymentMethodName));
                            serviceDto.setTotalAmount(
                                    serviceDto.getCashTotal() + serviceDto.getChequeTotal() + serviceDto.getPosTotal()
                                            + serviceDto.getEftTotal() + serviceDto.getMobileTotal());
                        } else {
                            this.setCashierShiftServiceDtoAmount(payment, item.getNetAmount(), dto, itemName);
                        }
                    } else {
                        CashierDetailedReportDto dto = new CashierDetailedReportDto();
                        dto.setServiceDepartmentName(serviceDepartment.getName());
                        this.setCashierShiftServiceDtoAmount(payment, item.getNetAmount(), dto, itemName);
                        list.add(dto);
                    }
                }
                break;
            case DRUG:
                for (PharmacyBillItem item : payment.getPatientBill().getPharmacyBillItems()) {
                    String revenueDepartmentName = DRUG.name();
                    Optional<CashierDetailedReportDto> first = list.stream()
                            .filter(o -> o.getServiceDepartmentName().equals(revenueDepartmentName)).findFirst();
                    String itemName = "DRUG PAYMENT";
                    if (first.isPresent()) {
                        CashierDetailedReportDto dto = first.get();
                        Optional<CashierShiftServiceDto> service = dto.getServiceList().stream()
                                .filter(o -> o.getTitle().equals(itemName)).findFirst();
                        if (service.isPresent()) {
                            CashierShiftServiceDto serviceDto = service.get();
                            serviceDto.setCashTotal(this.setServiceDtoAmount(serviceDto.getCashTotal(), CASH,
                                    item.getNetAmount(), paymentMethodName));
                            serviceDto.setChequeTotal(this.setServiceDtoAmount(serviceDto.getChequeTotal(),
                                    PaymentMethodEnum.CHEQUE, item.getNetAmount(), paymentMethodName));
                            serviceDto.setPosTotal(this.setServiceDtoAmount(serviceDto.getPosTotal(),
                                    PaymentMethodEnum.POS, item.getNetAmount(), paymentMethodName));
                            serviceDto.setEftTotal(this.setServiceDtoAmount(serviceDto.getEftTotal(),
                                    PaymentMethodEnum.ETF, item.getNetAmount(), paymentMethodName));
                            serviceDto.setMobileTotal(this.setServiceDtoAmount(serviceDto.getMobileTotal(),
                                    PaymentMethodEnum.MOBILE_MONEY, item.getNetAmount(), paymentMethodName));
                            serviceDto.setTotalAmount(
                                    serviceDto.getCashTotal() + serviceDto.getChequeTotal() + serviceDto.getPosTotal()
                                            + serviceDto.getEftTotal() + serviceDto.getMobileTotal());
                        } else {
                            this.setCashierShiftServiceDtoAmount(payment, item.getNetAmount(), dto, itemName);
                        }
                    } else {
                        CashierDetailedReportDto dto = new CashierDetailedReportDto();
                        dto.setServiceDepartmentName(revenueDepartmentName);
                        this.setCashierShiftServiceDtoAmount(payment, item.getNetAmount(), dto, itemName);
                        list.add(dto);
                    }
                }
                break;
            case DEPOSIT:
                Double depositAmount = payment.getDepositLog().getDepositAmount();
                Optional<CashierDetailedReportDto> first = list.stream()
                        .filter(o -> o.getServiceDepartmentName().equals(DEPOSIT.name())).findFirst();
                String depositItemName = "DEPOSIT PAYMENT";
                if (first.isPresent()) {
                    CashierDetailedReportDto dto = first.get();
                    Optional<CashierShiftServiceDto> service = dto.getServiceList().stream()
                            .filter(o -> o.getTitle().equals(depositItemName)).findFirst();
                    if (service.isPresent()) {
                        CashierShiftServiceDto sdto = service.get();
                        sdto.setCashTotal(
                                this.setServiceDtoAmount(sdto.getCashTotal(), CASH, depositAmount, paymentMethodName));
                        sdto.setPosTotal(
                                this.setServiceDtoAmount(sdto.getPosTotal(), POS, depositAmount, paymentMethodName));
                        sdto.setChequeTotal(this.setServiceDtoAmount(sdto.getChequeTotal(), CHEQUE, depositAmount,
                                paymentMethodName));
                        sdto.setEftTotal(
                                this.setServiceDtoAmount(sdto.getEftTotal(), ETF, depositAmount, paymentMethodName));
                        sdto.setMobileTotal(this.setServiceDtoAmount(sdto.getMobileTotal(), MOBILE_MONEY, depositAmount,
                                paymentMethodName));
                        sdto.setTotalAmount(sdto.getCashTotal() + sdto.getChequeTotal() + sdto.getPosTotal()
                                + sdto.getEftTotal() + sdto.getMobileTotal());
                    } else {
                        this.setCashierShiftServiceDtoAmount(payment, depositAmount, dto, depositItemName);
                    }
                } else {
                    CashierDetailedReportDto dto = new CashierDetailedReportDto();
                    dto.setServiceDepartmentName(DEPOSIT.name());
                    this.setCashierShiftServiceDtoAmount(payment, depositAmount, dto, depositItemName);
                    list.add(dto);
                }
                break;
        }
        return list;
    }

    /* generate cashier shift report pdf */
    public byte[] generateCashierShiftReport(CashierShiftSearchDto dto) {
        if (dto.getShiftId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NO Shift Provided For Report");
        }

        byte[] bytes = new byte[0];
        CashierShift shift = this.findOneById(dto.getShiftId());
        if (dto.getReportType().equals(CashierShiftReportDetailTypeEnum.SUMMARY)) {
            bytes = this.getCashierShiftReportSummarizedFormat(shift);
        } else if (dto.getReportType().equals(CashierShiftReportDetailTypeEnum.DETAILED)) {
            bytes = this.getCashierShiftReportDetailedFormat(shift);
        }
        return bytes;
    }

    /* Acknowledge cashier shift fund */
    public ResponseEntity<byte[]> acknowledgeCashierShiftFund(CashierFundReceptionDto dto) {
        this.onValidateBeforeAcknowledgeShiftFund(dto);
        CashierShift shift = this.findOneById(dto.getShift().getId());
        if (shift.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisConstant.CANNOT_ACKNOWLEDGE_OPEN_SHIFT);
        } else if (shift.getIsFundReceived()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisConstant.SHIFT_IS_ALREADY_RECONCILED);
        }

        try {
            Department department = this.departmentService.findOne(dto.getLocation().getId().get());
            User user = this.userService.findOneRaw(dto.getReceivedBy().getId().get());
            CashierFundReception fundReception = this.fundReceptionService.acknowledgeCashierShift(user, department,
                    shift);
            shift.setFundReception(fundReception);
            shift.setIsFundReceived(true);
            this.cashierShiftRepository.save(shift);
            byte[] bytes = this.getCashierShiftAcknowledgementReport(fundReception);
            return ResponseEntity.status(HttpStatus.OK).body(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /* compile cashier shift record and return report in bytes */
    public ResponseEntity<byte[]> compileCashierShifts(CashierCompiledShiftDto dto) {
        this.onValidateBeforeShiftCompile(dto);
        List<CashierShift> cashierShifts = new ArrayList<>();
        User compiledBy = this.userService.findOneRaw(dto.getCompiledBy().getId().get());
        Department location = this.departmentService.findOneRaw(dto.getLocation().getId());
        boolean isAnyNotAcknowledged = false;
        for (CashierShiftDto shiftDto : dto.getCashierShifts()) {
            CashierShift shift = this.findOneById(shiftDto.getId());
            if (!shift.getIsFundReceived()) {
                isAnyNotAcknowledged = true;
            } else {
                cashierShifts.add(shift);
            }
        }
        if (isAnyNotAcknowledged) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisConstant.ALL_SHIFT_MUST_BE_RECONCILED);
        }
        try {
            CashierCompiledShift compiledShift = this.compiledShiftService.saveShiftsCompilation(cashierShifts,
                    compiledBy, location);
            this.updateCashierShiftCompiledStatus(cashierShifts, compiledShift);
            byte[] bytes = this.getCashierCompilationReport(compiledShift.getId());
            return ResponseEntity.status(HttpStatus.OK).body(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ResponseEntity<byte[]> getAllShiftPerDayReport(CashierShiftSearchDto dto) {
        try {
            Map<String, Object> param = this.getAllShiftPerDayReportMap(dto);
            InputStream filePath = HmisReportFileEnum.SHIFT_CASHIER_ALL_SHIFT_PER_DAY
                    .absoluteFilePath(this.utilService);
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JREmptyDataSource());
            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return ResponseEntity.status(HttpStatus.OK).body(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getAllShiftPerDayReportMap(CashierShiftSearchDto dto) {
        LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
        LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
        List<CashierAllShiftPerDayReportDto> data = this.getAllShiftPerDayReportData(startDate, endDate);
        Map<String, Object> map = new HashMap<>();
        map.put("reportDataList", new JRBeanCollectionDataSource(data));
        map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
        map.put("logo", this.commonService.getLogoAsStream());
        map.put("reportHeader", "ALL SHIFT PER DAY REPORT");
        map.put("reportDateBetween", String.format("%s and %s", startDate.toString(), endDate.toString()));

        return map;
    }

    private List<CashierAllShiftPerDayReportDto> getAllShiftPerDayReportData(LocalDate startDate, LocalDate endDate) {
        List<CashierAllShiftPerDayReportDto> dtoList = new ArrayList<>();
        List<CashierShift> shifts = this.cashierShiftRepository
                .findAllByOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(endDate, startDate);
        if (!shifts.isEmpty()) {
            for (CashierShift shift : shifts) {
                CashierAllShiftPerDayReportDto dto = new CashierAllShiftPerDayReportDto();
                dto.setCashierName(shift.getCashier().getFullName());
                if (shift.getCashier().getDepartment() != null) {
                    dto.setCashierDepartment(shift.getCashier().getDepartment().getName());
                } else {
                    dto.setCashierDepartment("-");
                }
                dto.setShiftNumber(shift.getCode());
                dto.setShiftDate(shift.getOpenDate().toString());
                dto.setCashTotal(this.getSumAmountForCashierShiftByPaymentMethod(shift, CASH));
                dto.setChequeTotal(this.getSumAmountForCashierShiftByPaymentMethod(shift, CHEQUE));
                dto.setPosTotal(this.getSumAmountForCashierShiftByPaymentMethod(shift, POS));
                dto.setMobileTotal(this.getSumAmountForCashierShiftByPaymentMethod(shift, MOBILE_MONEY));
                dto.setEtfTotal(this.getSumAmountForCashierShiftByPaymentMethod(shift, ETF));
                dto.setTotalTotal(dto.getCashTotal() + dto.getChequeTotal() + dto.getPosTotal() + dto.getMobileTotal()
                        + dto.getEtfTotal());
                dto.setReceiptCount(shift.getReceiptCount());
                dto.setShiftStatus(shift.getIsActive() ? "OPEN" : "CLOSED");
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private byte[] getCashierCompilationReport(Long compileShiftId) {
        CashierCompiledShift compiledShift = this.compiledShiftService.findOneById(compileShiftId);
        try {
            Map<String, Object> param = this.prepCashierCompilationReport(compiledShift);
            InputStream filePath = HmisReportFileEnum.SHIFT_CASHIER_COMPILATION.absoluteFilePath(this.utilService);
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> prepCashierCompilationReport(CashierCompiledShift compiledShift) {
        List<CashierCompiledShiftReportDataDto> data = this.getCashierShiftCompiledTopDataList(compiledShift);
        List<CashierSummarizedReportDto> dataRevenue = this.getCashierShiftCompiledRevenueDataList(compiledShift);
        Map<String, Object> map = new HashMap<>();
        map.put("reportDataList", new JRBeanCollectionDataSource(data));
        map.put("reportRevenueDataList", new JRBeanCollectionDataSource(dataRevenue));
        map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
        map.put("logo", this.commonService.getLogoAsStream());
        map.put("reportHeader", "SHIFT COMPILATION REPORT");
        map.put("compilationNumber", compiledShift.getCode());
        map.put("compiledDate", compiledShift.getCompiledDate().toString());
        map.put("compiledTime", compiledShift.getCompiledTime().truncatedTo(ChronoUnit.SECONDS).toString());
        map.put("compiledBy", compiledShift.getCompiledBy().getFullName());
        return map;
    }

    private List<CashierCompiledShiftReportDataDto> getCashierShiftCompiledTopDataList(
            CashierCompiledShift compiledShift) {
        List<CashierCompiledShiftReportDataDto> mainList = new ArrayList<>();
        if (!compiledShift.getCashierShifts().isEmpty()) {
            for (CashierShift shift : compiledShift.getCashierShifts()) {
                CashierCompiledShiftReportDataDto dto = new CashierCompiledShiftReportDataDto();
                dto.setCashierName(shift.getCashier().getFullName());
                dto.setShiftDate(shift.getOpenDate().toString());
                dto.setShiftNumber(shift.getCode());
                dto.setTotalAmount(this.getTotalAmountFromCashierShift(shift));
                mainList.add(dto);
            }
        }
        return mainList;
    }

    private double getTotalAmountFromCashierShift(CashierShift shit) {
        double totalAmount = 0;
        if (!shit.getPayments().isEmpty()) {
            totalAmount = shit.getPayments().stream().mapToDouble(PatientPayment::getNetTotal).sum();
        }
        return totalAmount;
    }

    private List<CashierSummarizedReportDto> getCashierShiftCompiledRevenueDataList(
            CashierCompiledShift compiledShift) {
        List<CashierSummarizedReportDto> mainList = new ArrayList<>();
        if (!compiledShift.getCashierShifts().isEmpty()) {
            List<RevenueDepartment> revenueDepartmentList = this.revenueDepartmentService.findAllRaw();
            if (!revenueDepartmentList.isEmpty()) {
                for (RevenueDepartment revenueDepartment : revenueDepartmentList) {
                    CashierSummarizedReportDto reportDto = new CashierSummarizedReportDto();
                    reportDto.setRevenueDepartment(revenueDepartment.getName());
                    for (CashierShift shift : compiledShift.getCashierShifts()) {
                        double cash = 0;
                        double cheque = 0;
                        double pos = 0;
                        double etf = 0;
                        double mobile = 0;
                        double total = 0;
                        for (PatientPayment payment : shift.getPayments()) {
                            cash += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                                    revenueDepartment, CASH);
                            cheque += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                                    revenueDepartment, PaymentMethodEnum.CHEQUE);
                            pos += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                                    revenueDepartment, PaymentMethodEnum.POS);
                            etf += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                                    revenueDepartment, PaymentMethodEnum.ETF);
                            mobile += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                                    revenueDepartment, PaymentMethodEnum.MOBILE_MONEY);
                            total = cash + cheque + pos + etf + mobile;
                        }
                        reportDto.setCashTotal(reportDto.getCashTotal() + cash);
                        reportDto.setChequeTotal(reportDto.getChequeTotal() + cheque);
                        reportDto.setPosTotal(reportDto.getPosTotal() + pos);
                        reportDto.setEftTotal(reportDto.getEftTotal() + etf);
                        reportDto.setMobileTotal(reportDto.getMobileTotal() + mobile);
                        reportDto.setTotalAmount(reportDto.getTotalAmount() + total);
                    }
                    mainList.add(reportDto);
                }
            }
        }
        return mainList;
    }

    private void updateCashierShiftCompiledStatus(List<CashierShift> shifts, CashierCompiledShift compiledShift) {
        try {
            for (CashierShift shift : shifts) {
                shift.setIsShitCompiled(true);
                shift.setCompiledShift(compiledShift);
                this.cashierShiftRepository.save(shift);
            }
            this.compiledShiftService.updateCompiledShiftsRecord(shifts, compiledShift.getId());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void onValidateBeforeShiftCompile(CashierCompiledShiftDto dto) {
        if (dto.getCashierShifts() == null || dto.getCashierShifts().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cashier shifts is required");
        }

        if (dto.getCompiledBy() == null || !dto.getCompiledBy().getId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Compiled by is required");
        }

        if (dto.getLocation() == null || !dto.getLocation().getId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required");
        }
    }

    private void onValidateBeforeAcknowledgeShiftFund(CashierFundReceptionDto dto) {
        if (dto.getShift() == null | dto.getShift().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift is required");
        }
        if (dto.getReceivedBy() == null | !dto.getReceivedBy().getId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acknowledged by is required");
        }
        if (dto.getLocation() == null | !dto.getLocation().getId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required");
        }

    }

    private List<CashierShift> findCashierRecordByDateRange(CashierShiftSearchDto dto) {
        LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
        LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
        List<CashierShift> cashierShifts = new ArrayList<>();

        if (dto.getSearchBy() != null && dto.getSearchBy().equals(SearchShiftByEnum.SHIFT_NUMBER)) {
            if (dto.getShiftNumber() != null) {
                cashierShifts.add(this.findByShiftNumber(dto.getShiftNumber()));
            } else {
                cashierShifts = this.cashierShiftRepository
                        .findAllByOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(endDate, startDate);
            }
        } else if (dto.getSearchBy() != null && dto.getSearchBy().equals(SearchShiftByEnum.CASHIER_USERNAME)) {
            if (dto.getUserId() != null) {
                User cashier = this.userService.findOneRaw(dto.getUserId());
                cashierShifts = this.cashierShiftRepository
                        .findAllByCashierAndOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(cashier, endDate,
                                startDate);
            }
        }

        return cashierShifts;
    }

    private byte[] getCashierShiftAcknowledgementReport(CashierFundReception fundReception) {
        try {
            Map<String, Object> param = this.prepCashierShiftAcknowledgementReportMap(fundReception);
            InputStream filePath = HmisReportFileEnum.SHIFT_CASHIER_ACKNOWLEDGEMENT.absoluteFilePath(this.utilService);
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> prepCashierShiftAcknowledgementReportMap(CashierFundReception fundReception) {
        List<CashierShiftReconciliationReportDto> data = this.getCashierReconciliationReportData(fundReception);
        Map<String, Object> map = new HashMap<>();
        map.put("reportDataList", new JRBeanCollectionDataSource(data));
        map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
        map.put("logo", this.commonService.getLogoAsStream());
        map.put("reportHeader", "CASHIER SHIFT RECONCILIATION REPORT");
        return map;
    }

    private List<CashierShiftReconciliationReportDto> getCashierReconciliationReportData(
            CashierFundReception fundReception) {
        List<CashierShiftReconciliationReportDto> dtoList = new ArrayList<>();
        CashierShiftReconciliationReportDto dto = new CashierShiftReconciliationReportDto();
        dto.setShiftNumber(fundReception.getShift().getCode());
        dto.setReconDate(fundReception.getDate().toString());
        dto.setShiftDate(fundReception.getShift().getOpenDate().toString());
        dto.setCashierName(fundReception.getShift().getCashier().getFullName());
        dto.setReconciledBy(fundReception.getReceivedBy().getFullName());
        dto.setCashTotal(this.getSumAmountForCashierShiftByPaymentMethod(fundReception.getShift(), CASH));
        dto.setChequeTotal(this.getSumAmountForCashierShiftByPaymentMethod(fundReception.getShift(), CHEQUE));
        dto.setPosTotal(this.getSumAmountForCashierShiftByPaymentMethod(fundReception.getShift(), POS));
        dto.setEtfTotal(this.getSumAmountForCashierShiftByPaymentMethod(fundReception.getShift(), ETF));
        dto.setMobileTotal(this.getSumAmountForCashierShiftByPaymentMethod(fundReception.getShift(), MOBILE_MONEY));
        dto.setTotalAmount(dto.getCashTotal() + dto.getChequeTotal() + dto.getPosTotal() + dto.getEtfTotal()
                + dto.getMobileTotal());
        dtoList.add(dto);
        return dtoList;

    }

    private double getSumAmountForCashierShiftByPaymentMethod(CashierShift cashierShift, PaymentMethodEnum methodEnum) {
        return cashierShift.getPayments()
                .stream()
                .filter(payment -> payment.getPaymentMethod().getName().equals(methodEnum.title()))
                .mapToDouble(PatientPayment::getNetTotal)
                .sum();
    }

    private byte[] getCashierShiftReportSummarizedFormat(CashierShift shift) {
        try {
            Map<String, Object> param = this.prepCashierShiftSummarizedReportMap(shift);
            InputStream filePath = HmisReportFileEnum.SHIFT_CASHIER_REPORT_SUMMARY.absoluteFilePath(this.utilService);
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private byte[] getCashierShiftReportDetailedFormat(CashierShift shift) {
        try {
            Map<String, Object> param = this.prepCashierShiftDetailedReportMap(shift);
            InputStream filePath = HmisReportFileEnum.SHIFT_CASHIER_REPORT_DETAILED.absoluteFilePath(this.utilService);
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param,
                    new JRBeanCollectionDataSource(this.detailedReportDtoList));
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> prepCashierShiftDetailedReportMap(CashierShift shift) {
        List<CashierDetailedTopReportDto> data = this.getCashierShiftDetailedReportTableDataList(shift);
        Map<String, Object> map = new HashMap<>();
        map.put("reportDataList", new JRBeanCollectionDataSource(data));
        map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
        map.put("logo", this.commonService.getLogoAsStream());
        map.put("shiftOpenDate",
                String.format("%s | %s", shift.getOpenDate().toString(), shift.getOpenTime().toString()));
        map.put("reportHeader", String.format("%s [%s] - [%s]", "CASHIER DETAILED SHIFT REPORT",
                shift.getCashier().getUserName(), shift.getCode()));
        return map;
    }

    private Map<String, Object> prepCashierShiftSummarizedReportMap(CashierShift shift) {
        List<CashierSummarizedReportDto> data = this.getCashierShiftSummarizedReportTableDataList(shift);
        Map<String, Object> map = new HashMap<>();
        map.put("reportDataList", new JRBeanCollectionDataSource(data));
        map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
        map.put("logo", this.commonService.getLogoAsStream());
        map.put("shiftOpenDate",
                String.format("%s | %s", shift.getOpenDate().toString(), shift.getOpenTime().toString()));
        map.put("reportHeader", String.format("%s [%s] - [%s]", "CASHIER SHIFT REPORT",
                shift.getCashier().getUserName(), shift.getCode()));
        return map;
    }

    private List<CashierDetailedTopReportDto> getCashierShiftDetailedReportTableDataList(CashierShift shift) {
        List<CashierDetailedTopReportDto> dtoList = new ArrayList<>();
        this.detailedReportDtoList = new ArrayList<>();
        if (!shift.getPayments().isEmpty()) {
            for (PatientPayment payment : shift.getPayments()) {
                CashierDetailedTopReportDto dto = new CashierDetailedTopReportDto();
                dto.setTransactionDate(payment.getDate().toString());
                dto.setReceiptNumber(payment.getReceiptNumber());
                dto.setIsCancelled(payment.getIsCancelled());
                if (payment.getPaymentTypeForEnum().equals(DEPOSIT.name())) {
                    dto.setPayer(payment.getDepositLog().getPatient().getFullName());
                    dto.setPatientNumber(payment.getDepositLog().getPatient().getPatientNumber());
                } else {
                    if (payment.getBillPaymentOptionTypeEnum().equals(BillPaymentOptionTypeEnum.WALK_IN)) {
                        dto.setPayer(payment.getPatientBill().getWalkInPatient().getFullName());
                        dto.setPatientNumber("-");
                    } else {
                        dto.setPayer(payment.getPatientBill().getPatient().getFullName());
                        dto.setPatientNumber(payment.getPatientBill().getPatient().getPatientNumber());
                    }
                }
                dto.setLocation(payment.getLocation().getName());
                dto.setCashTotal(this.getPatientPaymentAmountByPaymentMethod(payment, CASH));
                dto.setChequeTotal(this.getPatientPaymentAmountByPaymentMethod(payment, PaymentMethodEnum.CHEQUE));
                dto.setEftTotal(this.getPatientPaymentAmountByPaymentMethod(payment, PaymentMethodEnum.ETF));
                dto.setPosTotal(this.getPatientPaymentAmountByPaymentMethod(payment, PaymentMethodEnum.POS));
                dto.setMobileTotal(
                        this.getPatientPaymentAmountByPaymentMethod(payment, PaymentMethodEnum.MOBILE_MONEY));
                dto.setTotalAmount(dto.getCashTotal() + dto.getChequeTotal() + dto.getEftTotal() + dto.getPosTotal()
                        + dto.getMobileTotal());
                this.addPaymentToServiceForDetailedReport(payment); // report data for collection summary by service
                                                                    // department
                dtoList.add(dto);
            }
        }
        this.detailedReportDtoList
                .forEach(dto -> dto.setServiceListCollection(new JRBeanCollectionDataSource(dto.getServiceList())));
        return dtoList;
    }

    private List<CashierSummarizedReportDto> getCashierShiftSummarizedReportTableDataList(CashierShift shift) {
        List<CashierSummarizedReportDto> dtoList = new ArrayList<>();
        List<RevenueDepartment> revenueDepartmentList = this.revenueDepartmentService.findAllRaw();
        if (!shift.getPayments().isEmpty() && !revenueDepartmentList.isEmpty()) {
            for (RevenueDepartment revenueDepartment : revenueDepartmentList) {
                CashierSummarizedReportDto dto = this.setSummarizedReportDto(revenueDepartment, shift);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private CashierSummarizedReportDto setSummarizedReportDto(RevenueDepartment revenueDepartment, CashierShift shift) {
        CashierSummarizedReportDto dto = new CashierSummarizedReportDto();
        dto.setRevenueDepartment(revenueDepartment.getName());
        double cash = 0;
        double cheque = 0;
        double pos = 0;
        double etf = 0;
        double mobile = 0;
        for (PatientPayment payment : shift.getPayments()) {
            cash += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment, revenueDepartment,
                    CASH);
            cheque += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                    revenueDepartment, PaymentMethodEnum.CHEQUE);
            pos += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment, revenueDepartment,
                    PaymentMethodEnum.POS);
            etf += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment, revenueDepartment,
                    PaymentMethodEnum.ETF);
            mobile += this.paymentService.getPaymentAmountByRevenueDepartmentAndPaymentMethod(payment,
                    revenueDepartment, PaymentMethodEnum.MOBILE_MONEY);
        }
        dto.setCashTotal(cash);
        dto.setChequeTotal(cheque);
        dto.setEftTotal(etf);
        dto.setPosTotal(pos);
        dto.setMobileTotal(mobile);
        dto.setTotalAmount(dto.getCashTotal() + dto.getChequeTotal() + dto.getEftTotal() + dto.getPosTotal()
                + dto.getMobileTotal());
        return dto;
    }

    private double setServiceDtoAmount(double initialAmount, PaymentMethodEnum methodEnum, double itemNetAmount,
            String paymentMethod) {
        double amount = 0;
        if (paymentMethod.equals(methodEnum.title())) {
            amount = initialAmount + itemNetAmount;
        } else {
            amount = initialAmount;
        }
        return amount;
    }

    private void addPaymentToServiceForDetailedReport(PatientPayment payment) {
        this.detailedReportDtoList = this.getPaymentAmountByServiceDepartmentAndPaymentMethod(payment);
    }

    private void setCashierShiftServiceDtoAmount(PatientPayment payment, double itemAmount,
            CashierDetailedReportDto dto, String dtoTitle) {
        CashierShiftServiceDto serviceDto = new CashierShiftServiceDto();
        serviceDto.setTitle(dtoTitle);
        serviceDto.setCashTotal(this.setServiceDtoAmount(0, CASH, itemAmount, payment.getPaymentMethod().getName()));
        serviceDto.setChequeTotal(this.setServiceDtoAmount(0, PaymentMethodEnum.CHEQUE, itemAmount,
                payment.getPaymentMethod().getName()));
        serviceDto.setPosTotal(
                this.setServiceDtoAmount(0, PaymentMethodEnum.POS, itemAmount, payment.getPaymentMethod().getName()));
        serviceDto.setEftTotal(
                this.setServiceDtoAmount(0, PaymentMethodEnum.ETF, itemAmount, payment.getPaymentMethod().getName()));
        serviceDto.setMobileTotal(this.setServiceDtoAmount(0, PaymentMethodEnum.MOBILE_MONEY, itemAmount,
                payment.getPaymentMethod().getName()));
        serviceDto.setTotalAmount(serviceDto.getCashTotal() + serviceDto.getChequeTotal() + serviceDto.getPosTotal()
                + serviceDto.getEftTotal() + serviceDto.getMobileTotal());
        dto.getServiceList().add(serviceDto);
    }

    private void setShiftReportAmountSumValue(List<CashierShiftDto> list, CashierShiftWrapperDto wrapperDto) {
        wrapperDto.setResultList(list);
        double cash = this.getSumFromCashierShiftDtoList(list, CASH);
        double cheque = this.getSumFromCashierShiftDtoList(list, PaymentMethodEnum.CHEQUE);
        double pos = this.getSumFromCashierShiftDtoList(list, PaymentMethodEnum.POS);
        double mobile = this.getSumFromCashierShiftDtoList(list, PaymentMethodEnum.MOBILE_MONEY);
        double etf = this.getSumFromCashierShiftDtoList(list, PaymentMethodEnum.ETF);

        wrapperDto.setCash(cash);
        wrapperDto.setCheque(cheque);
        wrapperDto.setPos(pos);
        wrapperDto.setMobile(mobile);
        wrapperDto.setEtf(etf);
        wrapperDto.setTotal(cash + cheque + pos + mobile + etf);
    }

    private CashierShiftDto mapCashierShiftModelToDto(CashierShift model) {
        CashierShiftDto dto = new CashierShiftDto();
        if (model.getId() != null) {
            dto.setId(model.getId());
            dto.setShiftNumber(model.getCode());
        }
        if (model.getCode() != null) {
            dto.setShiftNumber(model.getCode());
        }
        if (model.getOpenDate() != null) {
            dto.setOpenDate(model.getOpenDate());
        }
        if (model.getOpenTime() != null) {
            dto.setOpenTime(model.getOpenTime());
        }
        dto.setReceiptCount(model.getReceiptCount());
        if (model.getCloseDate() != null) {
            dto.setCloseDate(model.getCloseDate());
        }
        if (model.getCloseTime() != null) {
            dto.setCloseTime(model.getCloseTime());
        }
        if (model.getClosedByUser() != null) {
            dto.setClosedByUser(this.userService.mapModelToDto(model.getClosedByUser()));
        }
        if (model.getCashier() != null) {
            dto.setCashier(this.userService.mapModelToDto(model.getCashier()));
        }
        if (model.getDepartment() != null) {
            dto.setDepartment(this.departmentService.mapModelToDto(model.getDepartment()));
        }
        if (model.getCloseTypeEnum() != null) {
            dto.setCloseTypeEnum(model.getCloseTypeEnum().toString());
        }
        dto.setIsActive(model.getIsActive());
        dto.setIsClosedByCashier(model.getIsClosedByCashier());
        dto.setIsFundReceived(model.getIsFundReceived());
        dto.setIsShitCompiled(model.getIsShitCompiled());
        if (model.getIsShitCompiled() && model.getCompiledShift() != null) {
            dto.setCompiledShift(this.compiledShiftService.getCompiledShiftRecord(model.getCompiledShift()));
        }
        if (model.getIsFundReceived()) {
            dto.setFundReception(this.fundReceptionService.getFundReceptionRecordByShift(model));
        }

        /*
         * double cash = this.getAmountTotalForCashierShiftByPaymentMethod(model, CASH);
         * double cheque = this.getAmountTotalForCashierShiftByPaymentMethod(model,
         * PaymentMethodEnum.CHEQUE);
         * double pos = this.getAmountTotalForCashierShiftByPaymentMethod(model,
         * PaymentMethodEnum.POS);
         * double mobile = this.getAmountTotalForCashierShiftByPaymentMethod(model,
         * PaymentMethodEnum.MOBILE_MONEY);
         * double etf = this.getAmountTotalForCashierShiftByPaymentMethod(model,
         * PaymentMethodEnum.ETF);
         */

        double cash = this.getSumAmountForCashierShiftByPaymentMethod(model, CASH);
        double cheque = this.getSumAmountForCashierShiftByPaymentMethod(model, PaymentMethodEnum.CHEQUE);
        double pos = this.getSumAmountForCashierShiftByPaymentMethod(model, PaymentMethodEnum.POS);
        double mobile = this.getSumAmountForCashierShiftByPaymentMethod(model, PaymentMethodEnum.MOBILE_MONEY);
        double etf = this.getSumAmountForCashierShiftByPaymentMethod(model, PaymentMethodEnum.ETF);

        dto.setCash(cash);
        dto.setCheque(cheque);
        dto.setPos(pos);
        dto.setMobileMoney(mobile);
        dto.setEtf(etf);
        dto.setTotal(cash + cheque + pos + mobile + etf);
        return dto;
    }

    @Deprecated()
    /**/
    private double getAmountTotalForCashierShiftByPaymentMethod(CashierShift shift, PaymentMethodEnum methodName) {
        double sumValue = 0;
        if (!shift.getPayments().isEmpty()) {
            for (PatientPayment payment : shift.getPayments()) {
                sumValue += this.getPatientPaymentAmountByPaymentMethod(payment, methodName);
            }
        }
        return sumValue;
    }

    private double getPatientPaymentAmountByPaymentMethod(PatientPayment payment, PaymentMethodEnum methodName) {
        boolean methodEquals = payment.getPaymentMethod().getName().equals(methodName.title());
        if (methodEquals) {
            return payment.getNetTotal();
        }
        return 0;
    }

    private double getSumFromCashierShiftDtoList(List<CashierShiftDto> dtoList, PaymentMethodEnum paymentMethod) {
        double sum = 0;
        if (dtoList.isEmpty()) {
            return sum;
        }
        switch (paymentMethod) {
            case CASH:
                sum = dtoList.stream().mapToDouble(CashierShiftDto::getCash).sum();
                break;
            case CHEQUE:
                sum = dtoList.stream().mapToDouble(CashierShiftDto::getCheque).sum();
                break;
            case ETF:
                sum = dtoList.stream().mapToDouble(CashierShiftDto::getEtf).sum();
                break;
            case POS:
                sum = dtoList.stream().mapToDouble(CashierShiftDto::getPos).sum();
                break;
            case MOBILE_MONEY:
                sum = dtoList.stream().mapToDouble(CashierShiftDto::getMobileMoney).sum();
                break;
        }
        return sum;
    }

    private boolean isUserShiftOwner(Long userId, String code) {
        Optional<CashierShift> optionalCashierShift = this.findByCode(code);
        return optionalCashierShift.map(shift -> shift.getCashier().getId().equals(userId)).orElse(false);
    }

    private String generateShitCode() {
        GenerateCodeDto codeDto = new GenerateCodeDto();
        codeDto.setDefaultPrefix(HmisCodeDefaults.CASHIER_SHIFT_PREFIX);
        codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.CASHIER_SHIFT_CODE_PREFIX));
        codeDto.setLastGeneratedCode(this.cashierShiftRepository.findTopByOrderByIdDesc().map(CashierShift::getCode));
        return commonService.generateDataCode(codeDto);
    }

    private void closeShift(CashierShift shift, ShiftCloseTypeEnum typeEnum, LocalDate closeDate, LocalTime closeTime) {
        shift.setCloseDate(closeDate);
        shift.setCloseTime(closeTime);
        shift.setCloseTypeEnum(typeEnum);
        shift.setIsActive(false);
        this.cashierShiftRepository.save(shift);
    }

}
