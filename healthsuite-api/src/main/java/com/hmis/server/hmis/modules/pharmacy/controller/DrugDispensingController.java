package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugDispenseDto;
import com.hmis.server.hmis.modules.pharmacy.service.DrugDispenseServiceImpl;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/drug-dispensing" )
public class DrugDispensingController {
	@Autowired
	private DrugDispenseServiceImpl drugDispenseService;

	@PostMapping( value = "dispense-drug" )
	public ResponseEntity< ValidationResponse > dispenseDrug(@Valid @RequestBody DrugDispenseDto dispenseDto) {
		try {
			return this.drugDispenseService.dispenseDrugs(dispenseDto);
		}
		catch( Exception e ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
