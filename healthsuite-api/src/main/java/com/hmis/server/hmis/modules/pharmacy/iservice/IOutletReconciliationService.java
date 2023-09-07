package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletReconcileDto;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletStockItemDto;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisitionItem;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyOutletStock;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoodsItem;
import java.util.List;
import java.util.Optional;

public interface IOutletReconciliationService {
	// out is a department
	Optional< PharmacyOutletStock > findStockByDrugRegisterAndOutlet(DrugRegister drugRegister, Department department);

	PharmacyOutletStock findStockByDrugRegisterAndOutletRaw(Long drugRegisterId, Long departmentId);

	void reconcileOutletStock(OutletReconcileDto dto);

	OutletStockItemDto getOutletItemStock(Long outletId, Long drugId);

	List< OutletStockItemDto > searchOutletStockBalance(String searchTerm, Long outletId);

	void removeItemFromOutlet(DrugRegister drugRegister, Department department, int itemCount);

	void addStockFromSuppliersGoodsReceived(Department receivingOutlet, List< PharmacyReceivedGoodsItem > items, boolean isStore);

	void addStockToReceivingOutlet(List< DrugRequisitionItem > requisitionItems, Department receivingOutlet, boolean isStore);

	void removeStockFromIssuingOutlet(List< DrugRequisitionItem > requisitionItems, Department issuingOutlet);
}
