package com.hmis.server.hmis.modules.reports.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.PaymentMethod;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.service.*;
import com.hmis.server.hmis.common.constant.PaymentMethodEnum;
import com.hmis.server.hmis.modules.billing.model.PatientDepositLog;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.billing.service.DepositServiceImpl;
import com.hmis.server.hmis.modules.billing.service.PatientPaymentServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.service.DrugRegisterServiceImpl;
import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionResultDto;
import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import com.hmis.server.hmis.modules.reports.dto.DailyCollectionFilterTypeEnum;
import com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum;
import com.hmis.server.hmis.modules.reports.iservice.IAccountService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;

@Service
@Slf4j
public class AccountReportService implements IAccountService {

    private final RevenueDepartmentServiceImpl revenueDepartmentService;
    private final PatientPaymentServiceImpl paymentService;
    private final HmisUtilService utilService;
    private final PaymentMethodServiceImpl paymentMethodService;
    private final CommonService commonService;
    private final GlobalSettingsImpl globalSettingsService;
    private final DepositServiceImpl depositService;
    private final DepartmentServiceImpl departmentService;
    private final ProductServiceImpl productService;
    private final DrugRegisterServiceImpl drugRegisterService;

    private double cash = 0;
    private double cheque = 0;
    private double pos = 0;
    private double etf = 0;
    private double mobile = 0;
    private double cashAndPos = 0;
    private double cashAndCheque = 0;
    private double total = 0;

    private double cashGrandTotal = 0;
    private double chequeGrandTotal = 0;
    private double posGrandTotal = 0;
    private double etfGrandTotal = 0;
    private double mobileGrandTotal = 0;
    private double cashPosGrandTotal = 0;
    private double cashChequeGrandTotal = 0;
    private double totalSumGrandTotal = 0;

    @Autowired
    public AccountReportService(
            RevenueDepartmentServiceImpl revenueDepartmentService,
            PatientPaymentServiceImpl paymentService,
            HmisUtilService utilService,
            PaymentMethodServiceImpl paymentMethodService,
            CommonService commonService,
            GlobalSettingsImpl globalSettingsService,
            DepositServiceImpl depositService,
            DepartmentServiceImpl departmentService,
            ProductServiceImpl productService,
            DrugRegisterServiceImpl drugRegisterService) {
        this.revenueDepartmentService = revenueDepartmentService;
        this.paymentService = paymentService;
        this.utilService = utilService;
        this.paymentMethodService = paymentMethodService;
        this.commonService = commonService;
        this.globalSettingsService = globalSettingsService;
        this.depositService = depositService;
        this.departmentService = departmentService;
        this.productService = productService;
        this.drugRegisterService = drugRegisterService;
    }

    /* generate daily cash collection report */
    @Override
    public byte[] generateDailyCashCollection(DailyCashCollectionSearchDto dto) {
        try {
            this.validatePayload(dto);
            Map<String, Object> param = this.prepDailyCollectionReportParameters(dto);
            InputStream filePath = this.getReportFile();
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /* prepare daily cash collection report data & table values */
    private Map<String, Object> prepDailyCollectionReportParameters(DailyCashCollectionSearchDto dto) {
        /*
         * todo:: refactor search feature to use jpa criteria builder specification
         * refactor DailyCashCollectionResultDto remove duplicate fields for string and
         * double property
         */
        LocalDate start = this.utilService.transformToLocalDate(dto.getStartDate());
        LocalDate end = this.utilService.transformToLocalDate(dto.getEndDate());
        List<DailyCashCollectionResultDto> dataList = this.prepDailyCollectionReportDataList(dto, start, end);
        String filterType = dto.getType().name().toUpperCase();
        if (filterType.contains("_")) {
            filterType = filterType.replace("_", " ");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("reportDataList", new JRBeanCollectionDataSource(dataList));
        map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
        map.put("logo", this.commonService.getLogoAsStream());
        map.put("dateBetween", String.format("%s  -  %s", start, end));
        map.put("reportHeader", String.format("CASH COLLECTION REPORT  - BY %s", filterType));
        map.put("cashGrandTotal", this.utilService.formatAmount(cashGrandTotal));
        map.put("chequeGrandTotal", this.utilService.formatAmount(chequeGrandTotal));
        map.put("posGrandTotal", this.utilService.formatAmount(posGrandTotal));
        map.put("etfGrandTotal", this.utilService.formatAmount(etfGrandTotal));
        map.put("mobileGrandTotal", this.utilService.formatAmount(mobileGrandTotal));
        map.put("cashPosGrandTotal", this.utilService.formatAmount(cashPosGrandTotal));
        map.put("cashChequeGrandTotal", this.utilService.formatAmount(cashChequeGrandTotal));
        map.put("totalSumGrandTotal", this.utilService.formatAmount(totalSumGrandTotal));
        return map;
    }

    /* prepare daily cash collection report table dataset list */
    private List<DailyCashCollectionResultDto> prepDailyCollectionReportDataList(
            DailyCashCollectionSearchDto dto, LocalDate start, LocalDate end) {

        List<DailyCashCollectionResultDto> resultList = new ArrayList<>();
        List<PaymentMethod> paymentMethods = this.paymentMethodService.findAllRaw();

        if (dto.getType() != null && dto.getType().equals(DailyCollectionFilterTypeEnum.REVENUE_DEPARTMENT)) {
            resultList = this.prepDailyCollectionReportByRevenueFilter(dto, start, end, paymentMethods);
        } else if (dto.getType() != null && dto.getType().equals(DailyCollectionFilterTypeEnum.SERVICE_DEPARTMENT)) {
            resultList = this.prepDailyCollectionReportByServiceDepartmentFilter(start, end, paymentMethods, dto);
        } else if (dto.getType() != null && dto.getType().equals(DailyCollectionFilterTypeEnum.SERVICE)) {
            resultList = this.prepDailyCollectionReportByServiceFilter(dto, start, end, paymentMethods);
        } else if (dto.getType() != null && dto.getType().equals(DailyCollectionFilterTypeEnum.DRUG)) {
            resultList = this.prepDailyCollectionReportByDrugFilter(dto, start, end, paymentMethods);
        }
        this.setReportGrandTotalValue(resultList);
        return resultList;
    }

    /*
     * use revenue department to filter patient payment for daily cash collection
     * report
     */
    private List<DailyCashCollectionResultDto> prepDailyCollectionReportByRevenueFilter(
            DailyCashCollectionSearchDto dto,
            LocalDate start, LocalDate end, List<PaymentMethod> paymentMethods) {
        List<DailyCashCollectionResultDto> resultList = new ArrayList<>();
        List<PatientDepositLog> depositLogList = new ArrayList<>();
        List<RevenueDepartment> revenueList = this.revenueDepartmentService.findRevenueForDailyCollection(dto);
        List<PatientPayment> paymentList = this.paymentService.findPatientPaymentByDateRange(start, end);
        if (this.revenueDepartmentService.hasAnyDepositRevenueDepartment()) {
            depositLogList = this.depositService.findPatientDepositLogByDateRange(start, end);
        }
        int i = 1;

        if (!revenueList.isEmpty() && !paymentMethods.isEmpty()) {
            for (RevenueDepartment department : revenueList) {
                DailyCashCollectionResultDto resultDto = new DailyCashCollectionResultDto();
                String departmentName = department.getName();
                resultDto.setSerialNumber(String.valueOf(i++));
                resultDto.setRevenueDepartment(departmentName);
                this.resetSum();
                for (PaymentMethod method : paymentMethods) {
                    PaymentMethodEnum methodEnum = PaymentMethodEnum.getPaymentMethodEnumValue(method.getName());
                    if (this.revenueDepartmentService.isDepositRevenueDepartment(department)
                            && !depositLogList.isEmpty()) {
                        this.setDepositPaymentReportValue(depositLogList, methodEnum, resultDto);
                    } else {
                        this.setServiceOrDrugPaymentReportByRevenueDepartment(methodEnum, paymentList, department,
                                resultDto);
                    }
                }
                this.setReportTotalValue(resultDto);
                resultList.add(resultDto);
            }
        }
        return resultList;
    }

    /*
     * use service department to filter patient payment for daily cash collection
     * report
     */
    private List<DailyCashCollectionResultDto> prepDailyCollectionReportByServiceDepartmentFilter(
            LocalDate start,
            LocalDate end, List<PaymentMethod> paymentMethods, DailyCashCollectionSearchDto dto) {
        List<DailyCashCollectionResultDto> resultList = new ArrayList<>();
        List<Department> serviceDepartmentList = this.departmentService.getDepartmentForDailyCollectionReport(dto);
        List<PatientPayment> paymentList = this.paymentService.findPatientPaymentByDateRange(start, end);

        int i = 1;
        if (!serviceDepartmentList.isEmpty()) {
            for (Department department : serviceDepartmentList) {
                DailyCashCollectionResultDto resultDto = new DailyCashCollectionResultDto();
                String departmentName = department.getName();
                resultDto.setSerialNumber(String.valueOf(i++));
                resultDto.setRevenueDepartment(departmentName);
                this.resetSum();
                for (PaymentMethod method : paymentMethods) {
                    PaymentMethodEnum methodEnum = PaymentMethodEnum.getPaymentMethodEnumValue(method.getName());
                    this.setServiceOrDrugPaymentReportByServiceDepartment(methodEnum, paymentList, department,
                            resultDto);
                }
                this.setReportTotalValue(resultDto);
                resultList.add(resultDto);
            }
        }

        return resultList;
    }

    /*
     * use service name to filter patient payment for daily cash collection report
     */
    private List<DailyCashCollectionResultDto> prepDailyCollectionReportByServiceFilter(
            DailyCashCollectionSearchDto dto, LocalDate start,
            LocalDate end, List<PaymentMethod> paymentMethods) {
        List<DailyCashCollectionResultDto> resultList = new ArrayList<>();
        List<PatientPayment> paymentList = this.paymentService.findPatientPaymentByDateRange(start, end);
        int i = 1;

        List<ProductService> serviceList = this.productService.findServicesForDailyCashReport(dto);

        if (!serviceList.isEmpty()) {
            for (ProductService service : serviceList) {
                DailyCashCollectionResultDto resultDto = new DailyCashCollectionResultDto();
                resultDto.setSerialNumber(String.valueOf(i++));
                resultDto.setRevenueDepartment(service.getName());
                this.resetSum();
                for (PaymentMethod method : paymentMethods) {
                    PaymentMethodEnum methodEnum = PaymentMethodEnum.getPaymentMethodEnumValue(method.getName());
                    this.setServiceOrDrugPaymentReportByServiceName(methodEnum, paymentList, service, method,
                            resultDto);
                }
                this.setReportTotalValue(resultDto);
                resultList.add(resultDto);
            }
        }
        return resultList;
    }

    /* use drug to filter patient payment for daily cash collection report */
    private List<DailyCashCollectionResultDto> prepDailyCollectionReportByDrugFilter(
            DailyCashCollectionSearchDto dto, LocalDate start,
            LocalDate end, List<PaymentMethod> paymentMethods) {
        List<DailyCashCollectionResultDto> resultList = new ArrayList<>();
        List<PatientPayment> paymentList = this.paymentService.findPatientPaymentByDateRange(start, end);
        int i = 1;

        List<DrugRegister> drugList = this.drugRegisterService.findAllForDailyCashCollectionReport(dto);
        for (DrugRegister drug : drugList) {
            DailyCashCollectionResultDto resultDto = new DailyCashCollectionResultDto();
            resultDto.setSerialNumber(String.valueOf(i++));
            resultDto.setRevenueDepartment(String.format("%s %s", drug.getGenericName(), drug.getBrandName()));
            this.resetSum();
            for (PaymentMethod method : paymentMethods) {
                PaymentMethodEnum methodEnum = PaymentMethodEnum.getPaymentMethodEnumValue(method.getName());
                this.setServiceOrDrugPaymentReportByDrugName(methodEnum, paymentList, drug, resultDto);
            }
            this.setReportTotalValue(resultDto);
            resultList.add(resultDto);
        }
        return resultList;
    }

    /* set report value for deposit payment */
    private void setDepositPaymentReportValue(
            List<PatientDepositLog> depositLogList,
            PaymentMethodEnum methodName,
            DailyCashCollectionResultDto resultDto) {
        switch (methodName) {
            case CASH:
                cash = this.depositService.getDepositsSumByPaymentMethod(depositLogList, PaymentMethodEnum.CASH);
                resultDto.setCashTotal(this.utilService.formatAmount(cash));
                resultDto.setCashDouble(cash);
                break;
            case CHEQUE:
                cheque = this.depositService.getDepositsSumByPaymentMethod(depositLogList, PaymentMethodEnum.CHEQUE);
                resultDto.setChequeTotal(this.utilService.formatAmount(cheque));
                resultDto.setChequeDouble(cheque);
                break;
            case ETF:
                etf = this.depositService.getDepositsSumByPaymentMethod(depositLogList, PaymentMethodEnum.ETF);
                resultDto.setEftTotal(this.utilService.formatAmount(etf));
                resultDto.setEtfDouble(etf);
                break;
            case POS:
                pos = this.depositService.getDepositsSumByPaymentMethod(depositLogList, PaymentMethodEnum.POS);
                resultDto.setPosTotal(this.utilService.formatAmount(pos));
                resultDto.setPosDouble(pos);
                break;
            case MOBILE_MONEY:
                mobile = this.depositService.getDepositsSumByPaymentMethod(depositLogList,
                        PaymentMethodEnum.MOBILE_MONEY);
                resultDto.setMobileMoneyTotal(this.utilService.formatAmount(mobile));
                resultDto.setMobileDouble(mobile);
                break;
        }
    }

    /* set report value by revenue department */
    private void setServiceOrDrugPaymentReportByRevenueDepartment(
            PaymentMethodEnum methodName, List<PatientPayment> paymentList,
            RevenueDepartment department,
            DailyCashCollectionResultDto resultDto) {
        switch (methodName) {
            case CASH:
                cash = this.paymentService.getTotalAmountByRevenueDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.CASH);
                resultDto.setCashTotal(this.utilService.formatAmount(cash));
                resultDto.setCashDouble(cash);
                break;
            case CHEQUE:
                cheque = this.paymentService.getTotalAmountByRevenueDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.CHEQUE);
                resultDto.setChequeTotal(this.utilService.formatAmount(cheque));
                resultDto.setChequeDouble(cheque);
                break;
            case ETF:
                etf = this.paymentService.getTotalAmountByRevenueDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.ETF);
                resultDto.setEftTotal(this.utilService.formatAmount(etf));
                resultDto.setEtfDouble(etf);
                break;
            case POS:
                pos = this.paymentService.getTotalAmountByRevenueDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.POS);
                resultDto.setPosTotal(this.utilService.formatAmount(pos));
                resultDto.setPosDouble(pos);
                break;
            case MOBILE_MONEY:
                mobile = this.paymentService.getTotalAmountByRevenueDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.MOBILE_MONEY);
                resultDto.setMobileMoneyTotal(this.utilService.formatAmount(mobile));
                resultDto.setMobileDouble(mobile);
                break;
        }
    }

    /* set report value by service department */
    private void setServiceOrDrugPaymentReportByServiceDepartment(
            PaymentMethodEnum methodName, List<PatientPayment> paymentList,
            Department department,
            DailyCashCollectionResultDto resultDto) {
        switch (methodName) {
            case CASH:
                cash = this.paymentService.getTotalAmountByServiceDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.CASH);
                resultDto.setCashTotal(this.utilService.formatAmount(cash));
                resultDto.setCashDouble(cash);
                break;
            case CHEQUE:
                cheque = this.paymentService.getTotalAmountByServiceDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.CHEQUE);
                resultDto.setChequeTotal(this.utilService.formatAmount(cheque));
                resultDto.setChequeDouble(cheque);
                break;
            case ETF:
                etf = this.paymentService.getTotalAmountByServiceDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.ETF);
                resultDto.setEftTotal(this.utilService.formatAmount(etf));
                resultDto.setEtfDouble(etf);
                break;
            case POS:
                pos = this.paymentService.getTotalAmountByServiceDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.POS);
                resultDto.setPosTotal(this.utilService.formatAmount(pos));
                resultDto.setPosDouble(pos);
                break;
            case MOBILE_MONEY:
                mobile = this.paymentService.getTotalAmountByServiceDepartmentAndPaymentMethod(paymentList, department,
                        PaymentMethodEnum.MOBILE_MONEY);
                resultDto.setMobileMoneyTotal(this.utilService.formatAmount(mobile));
                resultDto.setMobileDouble(mobile);
                break;
        }
    }

    /* set report value by service list */
    private void setServiceOrDrugPaymentReportByServiceName(
            PaymentMethodEnum methodName, List<PatientPayment> paymentList,
            ProductService service, PaymentMethod method,
            DailyCashCollectionResultDto resultDto) {
        switch (methodName) {
            case CASH:
                cash = this.paymentService.getTotalAmountByServiceNameAndPaymentMethod(paymentList, service,
                        PaymentMethodEnum.CASH);
                resultDto.setCashTotal(this.utilService.formatAmount(cash));
                resultDto.setCashDouble(cash);
                break;
            case CHEQUE:
                cheque = this.paymentService.getTotalAmountByServiceNameAndPaymentMethod(paymentList, service,
                        PaymentMethodEnum.CHEQUE);
                resultDto.setChequeTotal(this.utilService.formatAmount(cheque));
                resultDto.setChequeDouble(cheque);
                break;
            case ETF:
                etf = this.paymentService.getTotalAmountByServiceNameAndPaymentMethod(paymentList, service,
                        PaymentMethodEnum.ETF);
                resultDto.setEftTotal(this.utilService.formatAmount(etf));
                resultDto.setEtfDouble(etf);
                break;
            case POS:
                pos = this.paymentService.getTotalAmountByServiceNameAndPaymentMethod(paymentList, service,
                        PaymentMethodEnum.POS);
                resultDto.setPosTotal(this.utilService.formatAmount(pos));
                resultDto.setPosDouble(pos);
                break;
            case MOBILE_MONEY:
                mobile = this.paymentService.getTotalAmountByServiceNameAndPaymentMethod(paymentList, service,
                        PaymentMethodEnum.MOBILE_MONEY);
                resultDto.setMobileMoneyTotal(this.utilService.formatAmount(mobile));
                resultDto.setMobileDouble(mobile);
                break;
        }
    }

    /* set report value by drug list */
    private void setServiceOrDrugPaymentReportByDrugName(
            PaymentMethodEnum methodName, List<PatientPayment> paymentList,
            DrugRegister drug,
            DailyCashCollectionResultDto resultDto) {
        switch (methodName) {
            case CASH:
                cash = this.paymentService.getTotalAmountByDrugAndPaymentMethod(paymentList, drug,
                        PaymentMethodEnum.CASH);
                // todo:: externalize method to set resultDto then reuse the method across all
                // reference places
                resultDto.setCashTotal(this.utilService.formatAmount(cash));
                resultDto.setCashDouble(cash);
                break;
            case CHEQUE:
                cheque = this.paymentService.getTotalAmountByDrugAndPaymentMethod(paymentList, drug,
                        PaymentMethodEnum.CHEQUE);
                resultDto.setChequeTotal(this.utilService.formatAmount(cheque));
                resultDto.setChequeDouble(cheque);
                break;
            case ETF:
                etf = this.paymentService.getTotalAmountByDrugAndPaymentMethod(paymentList, drug,
                        PaymentMethodEnum.ETF);
                resultDto.setEftTotal(this.utilService.formatAmount(etf));
                resultDto.setEtfDouble(etf);
                break;
            case POS:
                pos = this.paymentService.getTotalAmountByDrugAndPaymentMethod(paymentList, drug,
                        PaymentMethodEnum.POS);
                resultDto.setPosTotal(this.utilService.formatAmount(pos));
                resultDto.setPosDouble(pos);
                break;
            case MOBILE_MONEY:
                mobile = this.paymentService.getTotalAmountByDrugAndPaymentMethod(paymentList, drug,
                        PaymentMethodEnum.MOBILE_MONEY);
                resultDto.setMobileMoneyTotal(this.utilService.formatAmount(mobile));
                resultDto.setMobileDouble(mobile);
                break;
        }
    }

    /* reset sum values used in daily collection report aggregating the amount */
    private void resetSum() {
        cash = 0;
        cheque = 0;
        pos = 0;
        etf = 0;
        mobile = 0;
        cashAndPos = 0;
        cashAndCheque = 0;
        total = 0;

        cashGrandTotal = 0;
        chequeGrandTotal = 0;
        posGrandTotal = 0;
        etfGrandTotal = 0;
        mobileGrandTotal = 0;
        cashPosGrandTotal = 0;
        cashChequeGrandTotal = 0;
        totalSumGrandTotal = 0;
    }

    /* validate daily cash collection dto */
    private void validatePayload(DailyCashCollectionSearchDto dto) {
        if (dto.getStartDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is required");
        }

        if (dto.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is required");
        }
    }

    /* get report file path used by daily cash collection report */
    private InputStream getReportFile() {
        return HmisReportFileEnum.ACC_DAILY_CASH_COLLECTION.absoluteFilePath(this.utilService);
    }

    /* set daily cash collection report result sum */
    private void setReportTotalValue(DailyCashCollectionResultDto resultDto) {
        cashAndPos = Double.sum(cash, pos);
        cashAndCheque = Double.sum(cash, cheque);
        total = cash + cheque + pos + etf + mobile;

        resultDto.setCashAndPosTotal(this.utilService.formatAmount(cashAndPos));
        resultDto.setCashAndPosDouble(cashAndPos);

        resultDto.setCashAndChequeTotal(this.utilService.formatAmount(cashAndCheque));
        resultDto.setCashAndChequeDouble(cashAndCheque);

        resultDto.setTotalAmount(this.utilService.formatAmount(total));
        resultDto.setTotalDouble(total);
    }

    private void setReportGrandTotalValue(List<DailyCashCollectionResultDto> resultList) {
        if (!resultList.isEmpty()) {
            cashGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getCashDouble).sum();
            posGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getPosDouble).sum();
            chequeGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getChequeDouble).sum();
            mobileGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getMobileDouble).sum();
            etfGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getEtfDouble).sum();
            cashPosGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getCashAndPosDouble)
                    .sum();
            cashChequeGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getCashAndChequeDouble)
                    .sum();
            totalSumGrandTotal = resultList.stream().mapToDouble(DailyCashCollectionResultDto::getTotalDouble).sum();
        }
    }
}
