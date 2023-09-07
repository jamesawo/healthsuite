package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.*;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.constant.PaymentMethodEnum;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.dto.CancelReceiptDto;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.dto.ReceiptTypeEnum;
import com.hmis.server.hmis.modules.billing.iservice.IPatientPaymentService;
import com.hmis.server.hmis.modules.billing.model.*;
import com.hmis.server.hmis.modules.billing.repository.CancelledPaymentRepository;
import com.hmis.server.hmis.modules.billing.repository.PatientPaymentRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.pharmacy.service.DrugDispenseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.*;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.TRUE;
import static com.hmis.server.hmis.modules.billing.dto.ReceiptTypeEnum.*;

@Service
@Slf4j
public class PatientPaymentServiceImpl implements IPatientPaymentService {
    private final PatientPaymentRepository patientPaymentRepository;
    private final CancelledPaymentRepository cancelledPaymentRepository;
    private final BillServiceImpl billService;
    private final DepositServiceImpl depositService;
    private final DrugDispenseServiceImpl drugDispenseService;
    private final GlobalSettingsImpl globalSettingsService;


    @Autowired
    public PatientPaymentServiceImpl(
            PatientPaymentRepository patientPaymentRepository,
            CancelledPaymentRepository cancelledPaymentRepository,
            BillServiceImpl billService,
            @Lazy DepositServiceImpl depositService,
            @Lazy DrugDispenseServiceImpl drugDispenseService,
            @Lazy GlobalSettingsImpl globalSettingsService) {
        this.patientPaymentRepository = patientPaymentRepository;
        this.cancelledPaymentRepository = cancelledPaymentRepository;
        this.billService = billService;
        this.depositService = depositService;
        this.drugDispenseService = drugDispenseService;
        this.globalSettingsService = globalSettingsService;
    }

    @Override
    public double getTotalAmountForBillByRevenueDepartment(
            PatientBill bill,
            RevenueDepartment revenueDepartment) {
        return this.billService.getBillTotalByRevenueDepartment(revenueDepartment, bill);
    }

    public double getTotalAmountByRevenueDepartmentAndPaymentMethod(
            List<PatientPayment> payments,
            RevenueDepartment department,
            PaymentMethodEnum method) {
        double total = 0.00;
        if (!payments.isEmpty()) {
            for (PatientPayment payment : payments) {
                if (payment.getPaymentMethod().getName().equals(method.title())) {
                    total += this.getTotalAmountForBillByRevenueDepartment(payment.getPatientBill(), department);
                }
            }
        }
        return total;
    }

    public double getTotalAmountByServiceDepartmentAndPaymentMethod(
            List<PatientPayment> payments,
            Department department,
            PaymentMethodEnum method) {
        double total = 0.00;
        if (!payments.isEmpty()) {
            for (PatientPayment payment : payments) {
                if (payment.getPaymentMethod().getName().equals(method.title())) {
                    total += this.billService.getBillTotalByServiceDepartment(department, payment.getPatientBill());
                }
            }
        }
        return total;
    }

    public double getTotalAmountByServiceNameAndPaymentMethod(
            List<PatientPayment> payments,
            ProductService service,
            PaymentMethodEnum method) {
        double total = 0.00;
        if (!payments.isEmpty()) {
            for (PatientPayment payment : payments) {
                if (payment.getPaymentMethod().getName().equals(method.title())) {
                    if (payment.getPatientBill() != null && !payment.getPatientBill().getPatientServiceBillItems().isEmpty()) {
                        for (PatientServiceBillItem item : payment.getPatientBill().getPatientServiceBillItems()) {
                            if (item.getProductService().getId().equals(service.getId())) {
                                total += item.getNetAmount();
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    public double getTotalAmountByDrugAndPaymentMethod(
            List<PatientPayment> payments,
            DrugRegister drug, PaymentMethodEnum method) {
        double total = 0.00;
        if (!payments.isEmpty()) {
            for (PatientPayment payment : payments) {
                if (payment.getPaymentMethod().getName().equals(method.title())) {
                    if (payment.getPatientBill() != null && !payment.getPatientBill().getPharmacyBillItems().isEmpty()) {
                        for (PharmacyBillItem item : payment.getPatientBill().getPharmacyBillItems()) {
                            if (item.getDrugRegister().getId().equals(drug.getId())) {
                                total += item.getNetAmount();
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    @Override
    public PatientPayment findById(Long id) {
        Optional<PatientPayment> payment = this.patientPaymentRepository.findById(id);
        if (!payment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }
        return payment.get();
    }

    @Override
    public PatientPayment savePatientPayment(PatientPayment patientPayment) {
        return this.patientPaymentRepository.save(patientPayment);
    }

    public List<PatientPayment> findPatientPaymentByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PatientPayment> resultList = new ArrayList<>();
        List<PatientPayment> searchList = this.patientPaymentRepository.findAllByDateIsLessThanEqualAndDateGreaterThanEqual(endDate, startDate);
        if (!searchList.isEmpty()) {
            resultList = searchList;
        }
        return resultList;
    }

    public PatientBillDto mapPatientBillToPatientBillDto(PatientBill patientBill) {
        return this.billService.mapPatientBillToDto(patientBill);
    }

    public double getPaymentAmountByRevenueDepartmentAndPaymentMethod(
            PatientPayment payment,
            RevenueDepartment department,
            PaymentMethodEnum methodEnum) {
        double amount = 0;
        PaymentTypeForEnum paymentType = PaymentTypeForEnum.getPaymentTypeForEnum(payment.getPaymentTypeForEnum());
        switch (paymentType) {
            case SERVICE:
                for (PatientServiceBillItem item : payment.getPatientBill().getPatientServiceBillItems()) {
                    if (item.getProductService().getRevenueDepartment().getId().equals(department.getId())) {
                        if (payment.getPaymentMethod().getName().equals(methodEnum.title())) {
                            amount += item.getNetAmount();
                        }
                    }
                }
                break;
            case DRUG:
                for (PharmacyBillItem item : payment.getPatientBill().getPharmacyBillItems()) {
                    if (item.getDrugRegister().getRevenueDepartment().getId().equals(department.getId())) {
                        if (payment.getPaymentMethod().getName().equals(methodEnum.title())) {
                            amount += item.getNetAmount();
                        }
                    }
                }
                break;
            case DEPOSIT:
                if (payment.getDepositLog().getRevenueDepartment().getId().equals(department.getId())) {
                    if (payment.getPaymentMethod().getName().equals(methodEnum.title())) {
                        amount += payment.getNetTotal();
                    }
                }
                break;
        }
        return amount;

    }

    public ResponseEntity<MessageDto> cancelPatientPayment(
            PaymentReceipt receipt,
            CancelReceiptDto dto) {
        PatientPayment payment = receipt.getPayment();
        if (payment != null) {
            ReceiptTypeEnum receiptTypeEnum = receipt.getReceiptTypeEnum();
            String comment = dto.getComment();
            User user = new User(dto.getCancelledBy().getId().get());
            Department department = new Department(dto.getCancelledFrom().getId().get());
            // checks receipt type: if it's a drug receipt then check if item is returned, etc..
            this.handleReceiptChecksBeforeCancelling(receiptTypeEnum, payment, receipt);
            payment.setCancelledPayment(this.saveCancelledPayment(payment, user, department, comment));
            payment.setIsCancelled(true);
            payment.setCancelledNetAmount(payment.getNetTotal());
            payment.setNetTotal(0.00);
            payment.setGrossTotal(0.00);
            payment.setDiscountTotal(0.00);
            payment.setWaivedTotal(0.00);
            this.billService.handleCancelPatientBill(payment.getPatientBill());
            this.patientPaymentRepository.save(payment);

            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("Cancelled Successfully."));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot Find Payment With Receipt!");
    }

    private void handleReceiptChecksBeforeCancelling(
            ReceiptTypeEnum receiptTypeEnum,
            PatientPayment payment,
            PaymentReceipt receipt)
    {
        PaymentTypeForEnum paymentTypeForEnum = PaymentTypeForEnum.getPaymentTypeForEnum(payment.getPaymentTypeForEnum());
        // if receipt is for drug payment, and stock & inventory is activated, check if drug has been returned;
        if (paymentTypeForEnum.equals(PaymentTypeForEnum.DRUG)) {
            if(this.globalSettingsService.findValueByKey(
                    ACTIVATE_STOCK_INVENTORY).equals(TRUE) &&
                    !this.isDrugReturned(receipt)
            ){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Drug Items Must Be Returned Before Cancelling Receipt");
            }
        }
        // if receipt is a service bill receipt, check if deposit was allocated
        else if( paymentTypeForEnum.equals(PaymentTypeForEnum.SERVICE) ){
            if (payment.getDepositAllocatedTotal() > 0){
                this.handleCancelledReceiptAllocatedDeposit(payment);
            }
        }
        // if receipt is deposit payment receipt, handle deposit removal
        else if ( paymentTypeForEnum.equals(PaymentTypeForEnum.DEPOSIT) ) {
            this.handleCancelledDepositPayment(payment);
        }
    }

    private void handleCancelledReceiptAllocatedDeposit(PatientPayment payment) {
        this.depositService.updateDepositSum(payment.getPatientBill().getPatient(),
                payment.getDepositAllocatedTotal());
    }

    private void handleCancelledDepositPayment(PatientPayment payment) {
        PatientDepositLog depositLog = payment.getDepositLog();
        PatientDetail patient = depositLog.getPatient();
        this.depositService.settleFromDepositSum(depositLog.getDepositAmount(), patient);
    }


    private boolean isDrugReturned( PaymentReceipt receipt) {
        // check if drug is dispensed first
        boolean drugDispensed = this.drugDispenseService.isDrugDispensed(receipt);
        if (drugDispensed){
            // check if drug is returned
            return this.drugDispenseService.isDrugReturned(receipt);
        }
       return true;
    }

    private CancelledPayment saveCancelledPayment(PatientPayment payment, User user, Department location, String comment) {
        CancelledPayment cancelledPayment = new CancelledPayment();
        cancelledPayment.setPayment(payment);
        cancelledPayment.setCancelledBy(user);
        cancelledPayment.setCancelledFrom(location);
        cancelledPayment.setComment(comment);
        cancelledPayment.setCancelledDate(LocalDate.now());
        cancelledPayment.setCancelledTime(LocalTime.now());
        cancelledPayment.setPaymentType(payment.getPatientBill().getBillTypeEnum());
        cancelledPayment.setCancelledNet(payment.getNetTotal());
        cancelledPayment.setCancelledGross(payment.getGrossTotal());
        cancelledPayment.setCancelledDiscount(payment.getDiscountTotal());
        cancelledPayment.setCancelledWaived(payment.getWaivedTotal());
        return this.cancelledPaymentRepository.save(cancelledPayment);
    }

}
