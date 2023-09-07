package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugDispenseDto;
import org.springframework.http.ResponseEntity;

public interface IDrugDispenseService {
	ResponseEntity< ValidationResponse > dispenseDrugs(DrugDispenseDto dispenseDto);
}
