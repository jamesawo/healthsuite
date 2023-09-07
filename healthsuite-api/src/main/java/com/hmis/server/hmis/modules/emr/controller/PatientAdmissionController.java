package com.hmis.server.hmis.modules.emr.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDischargeDto;
import com.hmis.server.hmis.modules.emr.dto.PatientWardTransferDto;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HmisConstant.API_PREFIX +"/admission-service")
public class PatientAdmissionController {

	private final PatientServiceImpl patientService;
	private final PatientAdmissionServiceImpl admissionService;

	@Autowired
	public PatientAdmissionController(
			PatientServiceImpl patientService,
			@Lazy PatientAdmissionServiceImpl admissionService) {
		this.patientService = patientService;
		this.admissionService = admissionService;
	}


	@PostMapping(value = "/admit-patient")
	public ResponseEntity<MessageDto> patientAdmission(@RequestBody PatientAdmissionDto admission){
		return this.patientService.patientAdmission(admission);
	}

	@PostMapping(value = "patient-discharge-pdf")
	public ResponseEntity<byte[]> patientDischarge(@RequestBody PatientDischargeDto dto) {
		return this.patientService.dischargePatientAndGenerateGatePass(dto);
	}

	@PostMapping(value = "transfer-patient-ward")
	public ResponseEntity<MessageDto> transferPatientWard(@RequestBody PatientWardTransferDto dto){
		return this.admissionService.transferPatientWard(dto);
	}

}
