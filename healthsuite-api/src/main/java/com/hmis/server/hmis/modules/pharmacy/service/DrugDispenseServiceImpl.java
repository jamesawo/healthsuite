package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum;
import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.dto.ReceiptTypeEnum;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.billing.model.PaymentReceipt;
import com.hmis.server.hmis.modules.billing.service.BillServiceImpl;
import com.hmis.server.hmis.modules.billing.service.PaymentReceiptServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugDispenseDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugReturnDto;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletStockItemDto;
import com.hmis.server.hmis.modules.pharmacy.iservice.IDrugDispenseService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugDispense;
import com.hmis.server.hmis.modules.pharmacy.model.DrugReturn;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.hmis.server.hmis.modules.pharmacy.repository.DrugDispenseRepository;
import com.hmis.server.hmis.modules.pharmacy.repository.DrugReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DrugDispenseServiceImpl implements IDrugDispenseService {
    private final PaymentReceiptServiceImpl receiptService;
    private final DepartmentServiceImpl departmentService;
    private final BillServiceImpl billService;
    private final OutletReconciliationServiceImpl outletReconciliationService;
    private final UserServiceImpl userService;
    private final DrugDispenseRepository drugDispenseRepository;
    private final DrugReturnRepository drugReturnRepository;

    @Autowired
    public DrugDispenseServiceImpl(
            PaymentReceiptServiceImpl receiptService,
            DepartmentServiceImpl departmentService,
            BillServiceImpl billService,
            OutletReconciliationServiceImpl outletReconciliationService,
            @Lazy UserServiceImpl userService,
            DrugDispenseRepository drugDispenseRepository,
            DrugReturnRepository drugReturnRepository) {
        this.receiptService = receiptService;
        this.departmentService = departmentService;
        this.billService = billService;
        this.outletReconciliationService = outletReconciliationService;
        this.userService = userService;
        this.drugDispenseRepository = drugDispenseRepository;
        this.drugReturnRepository = drugReturnRepository;
    }


    @Override
    public ResponseEntity<ValidationResponse> dispenseDrugs(DrugDispenseDto dispenseDto) {
        //todo:: add global setting to allow or disallow any outlet to dispense drug (if drugs was sold from that outlet or other outlet)
        if (dispenseDto.getDispensedBy() == null || !dispenseDto.getDispensedBy().getId().isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dispensed By is Required");
        }
        this.onValidateReceiptBeforeDispensing(dispenseDto.getReceiptId());
        this.onValidatePharmacyOutlet(dispenseDto.getOutlet().getId().get());

        return this.onProcessDrugDispense(dispenseDto.getDispensedBy().getId().get(), dispenseDto.getOutlet().getId().get(), dispenseDto.getPatientBillDto().getId(), dispenseDto.getReceiptId());
    }

    public DrugDispense findDispensedDrugByReceipt(PaymentReceipt receipt) {
        Optional<DrugDispense> optional = this.drugDispenseRepository.findByReceipt(receipt);
        return optional.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot Find Receipt In Drug Dispenser"));
    }

    public DrugReturn findDrugReturnByReceipt(PaymentReceipt receipt) {
        Optional<DrugReturn> optional  = this.drugReturnRepository.findByPaymentReceipt(receipt);
        return optional.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot Find Receipt In Drug Return"));
    }

    public ResponseEntity<MessageDto> handleDrugReturn(DrugReturnDto dto) {
        Department department = this.departmentService.findOne(dto.getReturnedFrom().getId().get());
        User user = this.userService.findOneRaw(dto.getReturnedBy().getId().get());
        PaymentReceipt receipt = this.receiptService.findOneRaw(dto.getPaymentReceiptId());
        DrugDispense drugDispense = this.findDispensedDrugByReceipt(receipt);

        if (!drugDispense.getOutlet().getId().equals(department.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Returning Outlet Is Not Same As Dispensary Outlet");
        }

        if (receipt.getReceiptTypeEnum().equals(ReceiptTypeEnum.DRUG_BILL_RECEIPT)) {
            List<PharmacyBillItem> items = receipt.getPayment().getPatientBill().getPharmacyBillItems();
            if (!items.isEmpty()) {
                for (PharmacyBillItem item : items) {
                    outletReconciliationService.addItemToOutlet(item.getDrugRegister(), department, item.getQuantity());
                }
            }
            this.saveDrugReturn(user, dto.getComment(), receipt, drugDispense.getOutlet() );
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Receipt Type For Drug Return");
    }

    public boolean isDrugReturned(PaymentReceipt receipt) {
        Optional<DrugReturn> optional  = this.drugReturnRepository.findByPaymentReceipt(receipt);
        return optional.isPresent();
    }

    public boolean isDrugDispensed(PaymentReceipt receipt) {
        Optional<DrugDispense> optional = this.drugDispenseRepository.findByReceipt(receipt);
        return optional.isPresent();
    }

    private void saveDrugReturn(
            User user,
            String comment,
            PaymentReceipt receipt,
            Department outlet){
        DrugReturn drugReturn = new DrugReturn();
        PatientPayment payment = receipt.getPayment();
        drugReturn.setPaymentReceipt(receipt);
        drugReturn.setReturnedByUser(user);
        drugReturn.setReturnedDate( LocalDate.now() );
        drugReturn.setReturnedTime( LocalTime.now() );
        drugReturn.setOutlet(outlet);
        drugReturn.setItemCount(payment.getPatientBill().getPharmacyBillItems().size());
        drugReturn.setReceiptNetAmount(payment.getNetTotal());
        drugReturn.setComment(comment);
        this.drugReturnRepository.save(drugReturn);
    }


    private void onValidateReceiptBeforeDispensing(Long receiptId) {
        boolean isValid = this.receiptService.isValidReceipt(receiptId);
        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt Is Invalid");
        }
        boolean isValidFor = this.receiptService.isValidReceiptFor(receiptId, PaymentTypeForEnum.DRUG);
        if (!isValidFor) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt Is Invalid For Dispensing");
        }
    }

    private void onValidatePharmacyOutlet(Long locationId) {
        boolean validLocationFor = this.departmentService.isValidLocationFor(locationId, DepartmentCategoryEnum.Pharmacy);
        if (!validLocationFor) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Pharmacy Location");
        }
    }

    private ResponseEntity<ValidationResponse> onProcessDrugDispense(Long userId, Long locationId, Long billId, Long receiptId) {
        try {
            ValidationResponse response = new ValidationResponse();
            PatientBill bill = this.billService.findById(billId);

            User user = this.userService.findOneRaw(userId);
            Department outlet = this.departmentService.findOne(locationId);
            PaymentReceipt receipt = this.receiptService.findOneRaw(receiptId);


            if (bill.getBillTypeEnum() == PaymentTypeForEnum.DRUG) {
                List<PharmacyBillItem> billItems = bill.getPharmacyBillItems();
                if (billItems.size() > 0) {
                    boolean canDispenseAll = false; // check outlet stock balance for all the items in the receipt before dispensing
                    for (PharmacyBillItem item : billItems) {
                        boolean canDispense = this.onCheckOutLetDrugBalance(locationId, item.getDrugRegister().getId(), item.getQuantity());
                        if (!canDispense) { // if outlet stock is lower on a particular item in the receipt
                            response.addMessage(item.getDrugRegister().getBrandName() + " " + item.getDrugRegister().getGenericName() + " Is Out Of Stock");
                        }
                        canDispenseAll = canDispense;
                    }

                    if (canDispenseAll) { // dispense all the items in the receipt
                        this.onDispenseReceiptItemFromStore(outlet, bill.getPharmacyBillItems()); // dispense items from store
                        this.receiptService.setReceiptIsUsedAfterDispensing(receiptId); //set receipt is used status
                        // save drug dispense record
                        this.saveDrugDispense(user, outlet, receipt);

                        response.addMessage("Receipt Items Dispensed Successfully!");
                        response.setStatus(true);
                        return ResponseEntity.ok(response);
                    } else {
                        response.addMessage("Receipt Items Dispensing Failed");
                        return ResponseEntity.badRequest().body(response);
                    }
                } else {
                    response.addMessage("No Receipt Bill Items Found");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            response.addMessage("Invalid Receipt Bill Type");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void saveDrugDispense(User user, Department outlet, PaymentReceipt receipt){
        DrugDispense drugDispense = new DrugDispense();
        drugDispense.setDispensedBy(user);
        drugDispense.setOutlet(outlet);
        drugDispense.setReceipt(receipt);
        this.drugDispenseRepository.save(drugDispense);
    }

    private boolean onCheckOutLetDrugBalance(Long departmentId, Long drugRegisterId, int quantityToDispense) {
        OutletStockItemDto outletItemStock = this.outletReconciliationService.getOutletItemStock(departmentId, drugRegisterId);
        return outletItemStock.getCurrentBalance() >= quantityToDispense;
    }

    private void onDispenseReceiptItemFromStore(Department outlet, List<PharmacyBillItem> items) {
        if (items.size() > 0) {
            for (PharmacyBillItem value : items) {
                this.outletReconciliationService.removeItemFromOutlet(value.getDrugRegister(), outlet, value.getQuantity());
            }
        }
    }

}
