package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRequisitionDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRequisitionItemDto;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisition;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface IDrugRequisitionService {
	DrugRequisition findOneDrugRequisition(Long requisitionId);

	ResponseEntity toStoreRequisition(DrugRequisitionDto drugRequisitionDto);

	ResponseEntity toOutletRequisition(DrugRequisitionDto drugRequisitionDto);

	ResponseEntity saveRequisition(DrugRequisitionDto drugRequisitionDto);

	@Transactional
	ResponseEntity createRequisition(DrugRequisitionDto drugRequisitionDto);

	void createManyRequisitionItems(List< DrugRequisitionItemDto > items, DrugRequisition drugRequisition);

	List< DrugRequisitionDto > findAllByOutletAndDateRange(DepartmentDto outlet, DateDto startDate, DateDto endDate);

	ResponseEntity grantRequisition(DrugRequisitionDto drugRequisitionDto);
}
