package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.modules.notification.model.Notification;
import com.hmis.server.hmis.modules.notification.service.NotificationServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRegisterDto;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletReconcileDto;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletStockItemDto;
import com.hmis.server.hmis.modules.pharmacy.iservice.IOutletReconciliationService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisitionItem;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyOutletStock;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoodsItem;
import com.hmis.server.hmis.modules.pharmacy.repository.PharmacyOutletStockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class OutletReconciliationServiceImpl implements IOutletReconciliationService {
    private final PharmacyOutletStockRepository outletStockRepository;
    @Lazy
    private final DrugRegisterServiceImpl drugRegisterService;
    private final DepartmentServiceImpl departmentService;
    private final DrugLowStockServiceImpl lowStockService;
    private final NotificationServiceImpl notificationService;

    @Autowired
    public OutletReconciliationServiceImpl(
            PharmacyOutletStockRepository outletStockRepository,
            DrugRegisterServiceImpl drugRegisterService,
            DepartmentServiceImpl departmentService,
            DrugLowStockServiceImpl lowStockService,
            NotificationServiceImpl notificationService
    ) {
        this.outletStockRepository = outletStockRepository;
        this.drugRegisterService = drugRegisterService;
        this.departmentService = departmentService;
        this.lowStockService = lowStockService;
        this.notificationService = notificationService;
    }


    // note: outlet is a department
    @Override
    public Optional<PharmacyOutletStock> findStockByDrugRegisterAndOutlet(DrugRegister drugRegister, Department department) {
        return this.outletStockRepository.findByDrugRegisterAndDepartment(drugRegister, department);
    }

    @Override
    public PharmacyOutletStock findStockByDrugRegisterAndOutletRaw(Long drugRegisterId, Long departmentId) {
        Optional<PharmacyOutletStock> stock = this.findStockByDrugRegisterAndOutlet(new DrugRegister(drugRegisterId), new Department(departmentId));
        if (!stock.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drug Cannot Be Found In Stock Record! ");
        }
        return stock.get();
    }

    @Override
    public void reconcileOutletStock(OutletReconcileDto dto) {
        if (dto.getItems().size() > 0) {
            for (OutletStockItemDto itemDto : dto.getItems()) {
                DrugRegister drugRegister = this.drugRegisterService.findOne(itemDto.getDrug().getId());
                Department department = this.departmentService.findOneRaw(dto.getOutlet().getId());
                int quantity = itemDto.getQuantityToReconcile();
                this.createOrUpdateStockBalance(drugRegister, department, quantity);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Items To Reconcile");
        }
    }

    @Override
    public OutletStockItemDto getOutletItemStock(Long outletId, Long drugId) {
        OutletStockItemDto outletStockItemDto = new OutletStockItemDto();

        DrugRegister drugRegister = this.drugRegisterService.findOne(drugId);
        Department department = this.departmentService.findOneRaw(Optional.of(outletId));
        Optional<PharmacyOutletStock> stock = this.outletStockRepository.findByDrugRegisterAndDepartment(drugRegister, department);

        if (stock.isPresent()) {
            outletStockItemDto.setCurrentBalance(stock.get().getBalance());
        } else {
            outletStockItemDto.setCurrentBalance(0);
        }
        outletStockItemDto.setDrug(this.drugRegisterService.mapToDto(drugRegister));
        return outletStockItemDto;
    }

    @Override
    public List<OutletStockItemDto> searchOutletStockBalance(String searchTerm, Long outletId) {
        List<OutletStockItemDto> outletStockItemDtos = new ArrayList<>();
        List<DrugRegisterDto> registerDtoList = this.drugRegisterService.searchDrugByBrandOrGenericName(searchTerm,
                true, outletId,
                false,
                null);
        if (registerDtoList.size() > 0) {
            for (DrugRegisterDto dto : registerDtoList) {
                OutletStockItemDto itemDto = new OutletStockItemDto();
                itemDto.setDrug(dto);
                itemDto.setCurrentBalance(dto.getAvailableQty());
                itemDto.setQuantityToReconcile(0);
                outletStockItemDtos.add(itemDto);
            }
        }
        return outletStockItemDtos;
    }

    @Override
    public void removeItemFromOutlet(DrugRegister drugRegister, Department department, int itemCount) {
        try {
            this.outletStockRepository.subtractDrugRegisterFromOutlet(department, drugRegister, itemCount);
            this.checkDrugReOrderLevel(department, drugRegister);    //check drug reorder level for stock alert
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void addItemToOutlet(DrugRegister drugRegister, Department department, int itemCount) {
        try {
            this.outletStockRepository.addDrugRegisterToOutletCount(department, drugRegister, itemCount);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @Override
    public void addStockFromSuppliersGoodsReceived(Department receivingOutlet, List<PharmacyReceivedGoodsItem> items, boolean isStore) {
        if (items != null && items.size() > 0) {
            for (PharmacyReceivedGoodsItem item : items) {
                if (isStore) {
                    this.createOrUpdateStockCountBalance(item.getDrugRegister(), receivingOutlet, item.getQuantityReceived());
                } else {
                    Integer unitOfIssue = item.getDrugRegister().getUnitOfIssue();
                    Integer packsPerPackingUnit = item.getDrugRegister().getPacksPerPackingUnit();
                    //	int multiPacks = unitOfIssue * packsPerPackingUnit;
                    //	this.createOrUpdateStockCountBalance(item.getDrugRegister(), receivingOutlet, item.getQuantityReceived() * multiPacks);

                    int countVal = this.mulUnitOfIssueByPckPerPacking(unitOfIssue, packsPerPackingUnit, item.getQuantityReceived());
                    this.createOrUpdateStockCountBalance(item.getDrugRegister(), receivingOutlet, countVal);
                }
            }
        }
    }

    @Override
    public void addStockToReceivingOutlet(List<DrugRequisitionItem> requisitionItems, Department receivingOutlet, boolean isStore) {
        if (requisitionItems != null && requisitionItems.size() > 0) {
            for (DrugRequisitionItem item : requisitionItems) {
                DrugRegister drugRegister = item.getDrugRegister();
                if (isStore) {
                    this.createOrUpdateStockCountBalance(drugRegister, receivingOutlet, item.getIssuingQuantity());
                } else {
                    int quantity = this.mulUnitOfIssueByPckPerPacking(drugRegister.getUnitOfIssue(), drugRegister.getPacksPerPackingUnit(), item.getIssuingQuantity());
                    this.createOrUpdateStockCountBalance(drugRegister, receivingOutlet, quantity);
                }
            }
        }
    }

    @Override
    public void removeStockFromIssuingOutlet(List<DrugRequisitionItem> requisitionItems, Department issuingOutlet) {
        if (requisitionItems != null && requisitionItems.size() > 0) {
            for (DrugRequisitionItem item : requisitionItems) {
                DrugRegister drugRegister = item.getDrugRegister();
                this.removeItemFromOutlet(drugRegister, issuingOutlet, item.getIssuingQuantity());
            }
        }
    }

    private int mulUnitOfIssueByPckPerPacking(int unitOfIssue, int pckPerPacking, int itemQty) {
        int unOfIssByPckPer = unitOfIssue * pckPerPacking;
        return itemQty * unOfIssByPckPer;
    }

    /**
     * after dispensing drug/item, check drug/item reorder level
     * if outlet drug/item stock balance is low, add to low stock count for scheduler alert
     */
    private void checkDrugReOrderLevel(Department department, DrugRegister drugRegister) {
        OutletStockItemDto outletItemStock = this.getOutletItemStock(department.getId(), drugRegister.getId());
        if (ObjectUtils.isNotEmpty(outletItemStock)) {
            if (outletItemStock.getCurrentBalance() <= drugRegister.getReorderLevel()) {
                this.lowStockService.addToLowStock(department, drugRegister);
                //send outlet low stock balance notification
                Notification notification = this.notificationService.prepareLowStockNotification(department, drugRegister);
                this.notificationService.saveAndSendNotification(notification);
            } else {
                this.lowStockService.removeLowStock(department, drugRegister);
            }
        }
    }

    private void createOrUpdateStockCountBalance(DrugRegister drugRegister, Department department, Integer count) {
        // will check if outlet has drug and update the quantity by adding count to quantity value
        Optional<PharmacyOutletStock> stock = this.findStockByDrugRegisterAndOutlet(drugRegister, department);
        if (stock.isPresent()) {
            this.outletStockRepository.addDrugRegisterToOutletCount(department, drugRegister, count);
        } else {
            PharmacyOutletStock outletStock = new PharmacyOutletStock();
            this.setPharmacyOutletStock(outletStock, department, drugRegister, count);
            this.outletStockRepository.save(outletStock);
        }
        //remove drug from low stock
        this.checkDrugReOrderLevel(department, drugRegister);
    }

    private void createOrUpdateStockBalance(DrugRegister drugRegister, Department department, Integer count) {
        // will check if outlet has drug then change stock balance to count value
        PharmacyOutletStock outletStock = new PharmacyOutletStock();
        this.setPharmacyOutletStock(outletStock, department, drugRegister, count);
        Optional<PharmacyOutletStock> stock = this.findStockByDrugRegisterAndOutlet(drugRegister, department);
        stock.ifPresent(pharmacyOutletStock -> outletStock.setId(pharmacyOutletStock.getId()));
        this.outletStockRepository.save(outletStock);
    }

    private void setPharmacyOutletStock(PharmacyOutletStock outletStock, Department department, DrugRegister drugRegister, Integer count) {
        outletStock.setDepartment(department);
        outletStock.setDrugRegister(drugRegister);
        outletStock.setBalance(count);
    }

}
