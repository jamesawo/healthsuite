package com.hmis.server.hmis.modules.emr.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.billing.dto.PatientBillStatsDto;
import com.hmis.server.hmis.modules.emr.dto.PatientRevisitDto;
import com.hmis.server.hmis.modules.emr.service.PatientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/emr" )
public class PatientRevisitController {

	@Autowired
	private PatientServiceImpl patientService;

	@PostMapping( "patient-revisit" )
	public ResponseDto revisitPatient(@Validated @RequestBody PatientRevisitDto dto) {
		return this.patientService.patientRevisit(dto);
	}

	@GetMapping(value = "patient-account-summary/{patientId}")
	public PatientBillStatsDto getAccountSummary(@PathVariable String patientId){
		return this.patientService.getRevisitAccountSummary(Long.parseLong(patientId));
	}
}

