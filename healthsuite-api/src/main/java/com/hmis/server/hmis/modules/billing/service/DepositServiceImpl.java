package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.PaymentMethodServiceImpl;
import com.hmis.server.hmis.common.common.service.RevenueDepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.PaymentMethodEnum;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.*;
import com.hmis.server.hmis.modules.billing.iservice.IDepositService;
import com.hmis.server.hmis.modules.billing.model.PatientDepositLog;
import com.hmis.server.hmis.modules.billing.model.PatientDepositSum;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.billing.repository.PatientDepositLogRepository;
import com.hmis.server.hmis.modules.billing.repository.PatientDepositSumRepository;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hmis.server.hmis.modules.shift.dto.CashierShiftSetterDto;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DepositServiceImpl implements IDepositService {

    private final PatientDepositSumRepository depositSumRepository;
    private final PatientDepositLogRepository depositLogRepository;
    private final PatientDetailServiceImpl patientService;
    private final DepartmentServiceImpl departmentService;
    private final UserServiceImpl userService;
    private final RevenueDepartmentServiceImpl revenueDepartmentService;
    private final PaymentMethodServiceImpl paymentMethodService;
    private final PaymentReceiptServiceImpl receiptService;
    private final PatientAdmissionServiceImpl admissionService;
    private final PatientPaymentServiceImpl paymentService;
    private final CashierShiftServiceImpl shiftService;

    private final InvoiceServiceImpl invoiceService;

    private PatientDetail classMemberPatientDetail = new PatientDetail();

    public DepositServiceImpl(
            PatientDepositSumRepository depositSumRepository,
            PatientDepositLogRepository depositLogRepository,
            PatientDetailServiceImpl patientService,
            DepartmentServiceImpl departmentService,
            UserServiceImpl userService,
            RevenueDepartmentServiceImpl revenueDepartmentService,
            PaymentMethodServiceImpl paymentMethodService,
            PaymentReceiptServiceImpl receiptService,
            PatientAdmissionServiceImpl admissionService,
            PatientPaymentServiceImpl paymentService,
            @Lazy
            CashierShiftServiceImpl shiftService,
            InvoiceServiceImpl invoiceService
    ) {
        this.depositSumRepository = depositSumRepository;
        this.depositLogRepository = depositLogRepository;
        this.patientService = patientService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.revenueDepartmentService = revenueDepartmentService;
        this.paymentMethodService = paymentMethodService;
        this.receiptService = receiptService;
        this.admissionService = admissionService;
        this.paymentService = paymentService;
        this.shiftService = shiftService;
        this.invoiceService = invoiceService;
    }

    @Transactional
    @Override
    public PaymentReceiptDto handlePatientDepositPayment(DepositDto depositDto) {
        try {
            classMemberPatientDetail = this.patientService.findPatientDetailById(depositDto.getPatientId());
            PatientDepositSum patientDepositSum = this.getOrCreatePatientDepositSum(classMemberPatientDetail);
            PatientDepositLog depositLog = this.createPatientDepositLog(depositDto, patientDepositSum);
            this.updateDepositSum(classMemberPatientDetail, depositDto.getDepositAmount());
            String receiptNumber = this.generateDepositReceiptNumber();
            PatientPayment patientPayment = this.updatePatientPaymentRecordAfterDepositPayment(depositLog, receiptNumber);
            // here
            byte[] bytes = this.receiptService.generateDepositPaymentReceipt(depositLog, receiptNumber, patientPayment);
            classMemberPatientDetail = null;
            return new PaymentReceiptDto(bytes);
        } catch (RuntimeException e) {
            // ( throw runtime exception ) roll back database transaction using @Transactional on method
            log.error(e.getMessage(), e);
            throw new RuntimeException("Deposit Failed, Try Again!");
        }
    }

    // Deposit Log
    @Override
    public PatientDepositLog createPatientDepositLog(DepositDto depositDto, PatientDepositSum patientDepositSum) {
        PatientDepositLog depositLog = new PatientDepositLog();
        depositLog.setPatientDepositSum(patientDepositSum);
        depositLog.setPatient(this.classMemberPatientDetail);
        boolean isOnAdmission = this.admissionService.isPatientOnAdmission(depositDto.getPatientId());
        if (isOnAdmission) {
            depositLog.setPatientAdmission(this.admissionService.findPatientAdmissionRaw(depositDto.getPatientId()));
        }
        this.mapToDepositLogModel(depositDto, depositLog);
        return this.depositLogRepository.save(depositLog);
    }

    // reduce allocated bill amount from deposit total
    public void settleFromDepositSum(Double totalBillAllocation, PatientDetail patientDetail) {
        this.depositSumRepository.subtractPatientDepositSumAmount(totalBillAllocation, patientDetail);
    }

    // Deposit Main
    @Override
    public PatientDepositSum getOrCreatePatientDepositSum(PatientDetail patientDetail) {
        //todo replace with findByPatientDetail
        boolean depositExist = this.isPatientDepositExist(patientDetail);
        if (!depositExist) {
            return this.createPatientDeposit(patientDetail);
        }
        return this.depositSumRepository.findPatientDepositSumByPatientDetail(patientDetail);
    }

    @Override
    public boolean isPatientDepositExist(PatientDetail patientDetail) {
        return this.depositSumRepository.existsByPatientDetail(patientDetail);
    }

    @Override
    public void updateDepositSum(PatientDetail patient, Double depositAmount) {
        this.depositSumRepository.addToPatientDepositSumAmount(depositAmount, patient);
    }

    @Override
    public DepositSumDto findPatientDepositSum(PatientDetail patient) {
        Optional<PatientDepositSum> optional = this.findDepositSumByPatientDetail(patient);
        return optional.map(this::mapDepositSumToDto).orElseGet(DepositSumDto::new);
    }

    @Override
    public Double findPatientDepositAmount(PatientDetail patientDetail) {
        Optional<PatientDepositSum> depositSum = this.depositSumRepository.findByPatientDetail(patientDetail);
        return !depositSum.isPresent() ? 0.00 : depositSum.get().getTotalDepositAmount();
    }

    @Override
    public Optional<PatientDepositSum> findDepositSumByPatientDetail(PatientDetail patientDetail) {
        return this.depositSumRepository.findByPatientDetail(patientDetail);
    }

    public double getDepositsSumByPaymentMethod(List<PatientDepositLog> depositLogList, PaymentMethodEnum method) {
        double sum = 0.00;
        if (!depositLogList.isEmpty()) {
            for (PatientDepositLog deposit : depositLogList) {
                sum += this.getDepositAmountByPaymentMethod(method, deposit);
            }
        }
        return sum;
    }

    public double getDepositAmountByPaymentMethod(PaymentMethodEnum method, PatientDepositLog deposit){
        if (method.title().equals(deposit.getPaymentMethod().getName())) {
            return deposit.getDepositAmount();
        }
        return 0;
    }

    private  PatientPayment  updatePatientPaymentRecordAfterDepositPayment(PatientDepositLog log, String receiptNumber) {
        PatientPayment model = new PatientPayment();
        model.setBillPaymentOptionTypeEnum(BillPaymentOptionTypeEnum.UN_BILLED_ITEM);
        model.setWaivedTotal(0.00);
        model.setGrossTotal(0.00);
        model.setDiscountTotal(0.00);
        model.setDepositAllocatedTotal(0.00);
        model.setNetTotal(log.getDepositAmount());
        model.setLocation(log.getDepartment());
        model.setPaymentMethod(log.getPaymentMethod());
        model.setCashier(log.getUser());
        model.setDate(LocalDate.now());
        model.setTime(LocalTime.now());
        model.setPaymentTypeForEnum(PaymentTypeForEnum.DEPOSIT.name());
        CashierShift cashierShift = this.shiftService.findAndIncrementReceiptCount(new CashierShiftSetterDto(log.getDepartment(), log.getUser()));
        model.setCashierShift(cashierShift);
        model.setPatientBill(null); // no patient bill because this is a deposit payment.
        model.setReceiptNumber(receiptNumber);
        model.setDepositLog(log);
        Optional<PatientAdmission> optionalAdmission = this.admissionService.findActiveAdmissionByPatientId(log.getPatient().getId());
        optionalAdmission.ifPresent(model::setAdmission);
        return this.paymentService.savePatientPayment(model);
    }

    public List<PatientDepositLog> findPatientDepositLogByDateRange(LocalDate start, LocalDate end) {
        List<PatientDepositLog> depositLogList = new ArrayList<>();
        List<PatientDepositLog> list = this.depositLogRepository.findAllByDateIsLessThanEqualAndDateGreaterThanEqual(end, start);
        if (!list.isEmpty()) {
            depositLogList = list;
        }
        return depositLogList;
    }

    private DepositSumDto mapDepositSumToDto(PatientDepositSum depositSum) {
        DepositSumDto depositSumDto = new DepositSumDto();
        depositSumDto.setId(depositSum.getId());
        depositSumDto.setTotalAmount(depositSum.getTotalDepositAmount());
        return depositSumDto;
    }

    private PatientDepositSum createPatientDeposit(PatientDetail patientDetail) {
        return this.depositSumRepository.save(new PatientDepositSum(patientDetail, 0.0));
    }

    private void mapToDepositLogModel(DepositDto dto, PatientDepositLog log) {

        if (dto.getLocationId() != null) {
            log.setDepartment(this.departmentService.findOneRaw(Optional.of(dto.getLocationId())));
        }

        if (dto.getUserId() != null) {
            log.setUser(this.userService.findOneRaw(dto.getUserId()));
        }

        if (dto.getRevenueDepartmentId() != null) {
            log.setRevenueDepartment(this.revenueDepartmentService.findOneRaw(dto.getRevenueDepartmentId()));
        }

        if (dto.getPaymentMethodId() != null) {
            log.setPaymentMethod(this.paymentMethodService.findOneRaw(dto.getPaymentMethodId()));
            if (dto.getTransactionRefNumber() != null) {
                log.setPosTransactRef(dto.getTransactionRefNumber());
            }

        }

        if (dto.getDepositAmount() != null) {
            log.setDepositAmount(dto.getDepositAmount());
        }

        if (dto.getDescription() != null) {
            log.setDescription(dto.getDescription());
        } else {
            throw new RuntimeException("Deposit Description is Required");
        }

    }

    private String generateDepositReceiptNumber() {
        return this.invoiceService.generateDepositReceiptNumber();
    }

}
