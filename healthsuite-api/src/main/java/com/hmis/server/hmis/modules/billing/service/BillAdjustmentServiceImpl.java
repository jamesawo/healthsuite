package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.BillItemDto;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.iservice.IBillAdjustmentService;
import com.hmis.server.hmis.modules.billing.model.PatientAdjustedBill;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.billing.repository.PatientAdjustedBillRepository;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyBillItemDto;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.pharmacy.service.PharmacyBillItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillAdjustmentServiceImpl implements IBillAdjustmentService {
    private final PatientAdjustedBillRepository repository;
    private final BillServiceImpl billService;
    private final BillItemServiceImpl billItemService;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;
    private final PharmacyBillItemServiceImpl pharmacyBillItemService;

    @Autowired
    public BillAdjustmentServiceImpl(
            PatientAdjustedBillRepository repository,
            @Lazy BillServiceImpl billService,
            @Lazy BillItemServiceImpl billItemService,
            @Lazy UserServiceImpl userService,
            @Lazy DepartmentServiceImpl departmentService,
            @Lazy PharmacyBillItemServiceImpl pharmacyBillItemService
    ) {
        this.repository = repository;
        this.billService = billService;
        this.billItemService = billItemService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.pharmacyBillItemService = pharmacyBillItemService;
    }

    public ResponseEntity<MessageDto> saveAdjustedBill(PatientBillDto dto) {
        this.validateBillBeforeAdjustment(dto);
        try {
            PatientBill bill = this.billService.findById(dto.getId());
            PaymentTypeForEnum type = bill.getBillTypeEnum();
            if (type.equals(PaymentTypeForEnum.SERVICE)){
                this.adjustServiceBill(bill, dto);
            }else if (type.equals(PaymentTypeForEnum.DRUG)){
                this.adjustDrugBill(bill, dto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void adjustServiceBill(PatientBill bill,  PatientBillDto dto){
        List<BillItemDto> dtoItems = dto.getBillItems();
        if (!dtoItems.isEmpty()){
            List<PatientServiceBillItem> modelItems = new ArrayList<>();
            for (BillItemDto billItemDto : dtoItems) {
                if (billItemDto.getIsAdjusted()){
                    PatientServiceBillItem serviceBillItem = this.billItemService.getOneById(billItemDto.getId());
                    serviceBillItem.setIsAdjusted(true);
                    serviceBillItem.setPrevQty(billItemDto.getQuantity());
                    serviceBillItem.setQuantity(billItemDto.getNewQuantity());
                    billItemDto.setQuantity(billItemDto.getNewQuantity());
                    this.billItemService.mapToModel(billItemDto, serviceBillItem, bill); // to recalculate other amount values
                    PatientServiceBillItem billItem = this.billItemService.saveBill(serviceBillItem);
                    this.setItemAfterServiceBillAdjustment(bill, billItem);
                }
            }

            PatientAdjustedBill adjustedBill = this.saveBillAdjustment(bill, dto);
            double netTotal = bill.getPatientServiceBillItems().stream().mapToDouble(PatientServiceBillItem::getNetAmount).sum();
            double grossTotal = bill.getPatientServiceBillItems().stream().mapToDouble(PatientServiceBillItem::getGross).sum();
            double discountTotal = bill.getPatientServiceBillItems().stream().mapToDouble(PatientServiceBillItem::getDiscountAmount).sum();
            this.billService.saveAdjustedBill(bill, adjustedBill, grossTotal, netTotal, discountTotal);
        }
    }

    private void setItemAfterServiceBillAdjustment(PatientBill bill, PatientServiceBillItem billItem) {
        for (int i = 0; i < bill.getPatientServiceBillItems().size(); i++) {
            if ( bill.getPatientServiceBillItems().get(i).getId().equals(billItem.getId()) ) {
                bill.getPatientServiceBillItems().set(i, billItem);
            }
        }
    }

    private void adjustDrugBill(PatientBill bill,  PatientBillDto dto){
        List<PharmacyBillItemDto> items = dto.getPharmacyBillItems();
        if (!items.isEmpty()){
            for (PharmacyBillItemDto item : items) {
                if (item.getIsAdjusted()){
                    PharmacyBillItem pharmacyBillItem = this.pharmacyBillItemService.getOneById(item.getId());
                    pharmacyBillItem.setIsAdjusted(true);
                    pharmacyBillItem.setPrevQty(Double.valueOf(item.getQuantity()));
                    pharmacyBillItem.setQuantity(item.getNewQuantity().intValue());
                    item.setQuantity(pharmacyBillItem.getQuantity()); // for mapping
                    this.pharmacyBillItemService.mapPharmacyBillItemDtoToModel(item, pharmacyBillItem, bill);
                    PharmacyBillItem billItem = this.pharmacyBillItemService.saveBill(pharmacyBillItem);
                    this.setItemAfterDrugBillAdjustment(bill, billItem);
                }
            }
            PatientAdjustedBill adjustedBill = this.saveBillAdjustment(bill, dto);
            double netTotal = bill.getPharmacyBillItems().stream().mapToDouble(PharmacyBillItem::getNetAmount).sum();
            double grossTotal = bill.getPharmacyBillItems().stream().mapToDouble(PharmacyBillItem::getGrossAmount).sum();
            double discountTotal = bill.getPharmacyBillItems().stream().mapToDouble(PharmacyBillItem::getDiscountAmount).sum();
            this.billService.saveAdjustedBill(bill, adjustedBill, grossTotal, netTotal, discountTotal);
        }
    }

    private void setItemAfterDrugBillAdjustment(PatientBill bill, PharmacyBillItem billItem) {
        for (int i = 0; i < bill.getPharmacyBillItems().size(); i++) {
            if ( bill.getPharmacyBillItems().get(i).getId().equals(billItem.getId()) ) {
                bill.getPharmacyBillItems().set(i, billItem);
            }
        }
    }

    private PatientAdjustedBill saveBillAdjustment(PatientBill bill, PatientBillDto dto) {
        User user = this.userService.findOneRaw(dto.getCostByDto().getId().get());
        Department location = this.departmentService.findOne(dto.getLocationDto().getId().get());
        PatientAdjustedBill adjustedBill = new PatientAdjustedBill();
        adjustedBill.setAdjustedBy(user);
        adjustedBill.setComment(dto.getComment());
        adjustedBill.setOldNetTotal(bill.getNetTotal());
        adjustedBill.setNewNetTotal(dto.getBillTotal().getNetTotal());
        adjustedBill.setDate(LocalDate.now());
        adjustedBill.setTime(LocalTime.now());
        adjustedBill.setPatientBill(bill);
        adjustedBill.setLocation(location);
        return this.repository.save(adjustedBill);
    }

    private void validateBillBeforeAdjustment(PatientBillDto dto) {
        if (dto.getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bill is required");
        }
        if (dto.getLocationDto() == null || !dto.getLocationDto().getId().isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required");
        }
        if (dto.getCostByDto() == null || !dto.getCostByDto().getId().isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adjusted by user is required");
        }
        if (dto.getComment() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment is required");
        }
    }
}
