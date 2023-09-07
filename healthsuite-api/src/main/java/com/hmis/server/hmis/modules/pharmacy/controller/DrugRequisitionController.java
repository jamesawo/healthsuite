package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRequisitionDto;
import com.hmis.server.hmis.modules.pharmacy.service.DrugRequisitionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/drug-requisition" )
public class DrugRequisitionController {
	@Autowired
	private DrugRequisitionServiceImpl requisitionService;

	@PostMapping( value = "to-store-req" )
	public ResponseEntity createToStoreRequisition(@RequestBody DrugRequisitionDto drugRequisitionDto) {
		return this.requisitionService.toStoreRequisition(drugRequisitionDto);
	}

	@PostMapping( value = "to-outlet-req" )
	public ResponseEntity createToOutletRequisition(@RequestBody DrugRequisitionDto drugRequisitionDto) {
		return this.requisitionService.toOutletRequisition(drugRequisitionDto);
	}

	@PostMapping( value = "save-requisition" )
	public ResponseEntity saveRequisition(@RequestBody DrugRequisitionDto drugRequisitionDto) {
		return this.requisitionService.saveRequisition(drugRequisitionDto);
	}

	@PostMapping( value = "grant-requisition" )
	public ResponseEntity grantRequisition(@RequestBody DrugRequisitionDto drugRequisitionDto) {
		return this.requisitionService.grantRequisition(drugRequisitionDto);
	}
}
