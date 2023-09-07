package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugOrderDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugOrderItemDto;
import com.hmis.server.hmis.modules.pharmacy.model.DrugOrder;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface IDrugOrderService {
	DrugOrder findOne(Long drugOrderId);

	boolean isDrugOrderExistAndMatch(Department department, DrugOrder drugOrder);

	boolean isDrugOrderExist(Department department);

	ResponseEntity< ValidationResponse > createDrugOrder(DrugOrderDto dto);

	String generateDrugOrderCode();

	List< DrugOrderItemDto > createManyDrugOrderItem(List< DrugOrderItemDto > items, DrugOrder drugOrder);

	void replaceAllDrugOrderItem(List< DrugOrderItemDto > items, DrugOrder drugOrder);

	List< DrugOrderDto > findDrugOrderByOrderCode(String code);

	DrugOrderDto mapDrugOrderModelToDto(DrugOrder model);

	ResponseEntity< ValidationResponse > updateDrugOrder(DrugOrderDto dto);

	void setDrugOrderFulfilledStatus(Long id);
}
