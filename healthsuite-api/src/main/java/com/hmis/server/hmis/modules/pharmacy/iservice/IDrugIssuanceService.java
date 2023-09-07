package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.modules.pharmacy.dto.DrugIssuanceDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRequisitionDto;
import com.hmis.server.hmis.modules.pharmacy.model.DrugIssuance;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisition;
import java.util.List;

public interface IDrugIssuanceService {
	DrugIssuance findById(Long id);

	DrugIssuance saveNewIssuance(DrugIssuanceDto drugIssuanceDto);

	DrugIssuanceDto saveIssuanceFromRequisition(DrugRequisition requisition);

	List< DrugRequisitionDto > findByOutletAndDateRange(DrugIssuanceDto drugIssuanceDto);
}
