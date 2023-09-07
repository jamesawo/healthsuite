package com.hmis.server.hmis.modules.emr.controller;

import com.hmis.server.hmis.common.common.dto.NationalityDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryUpdate;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.service.PatientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/emr" )
public class PatientRegistrationController {

	private final PatientServiceImpl registrationService;

	@Autowired
	public PatientRegistrationController( PatientServiceImpl registrationService ) {
		this.registrationService = registrationService;
	}

	@GetMapping( "search-patient-details" )
	public List<PatientDetailDto> searchPatient(
			@RequestParam( value = "search" ) String search,
			@RequestParam( value = "checkAdmission" ) boolean checkAdmission,
			@RequestParam( value = "loadAdmission" ) boolean loadAdmission,
			@RequestParam( value = "loadRevisit" ) boolean loadRevisit,
			@RequestParam( value = "loadSchemeDiscount" ) boolean loadSchemeDiscount,
			@RequestParam( value = "loadDeposit" ) boolean loadDeposit,
			@RequestParam( value = "loadDrugRequest", required = false ) boolean loadDrugRequest,
			@RequestParam( value = "loadLabRequest", required = false ) boolean loadLabRequest,
			@RequestParam( value = "loadRadiologyRequest", required = false ) boolean loadRadiologyRequest
	) {
		return this.registrationService.patientRecordTypeaheadSearch( search,
				checkAdmission,
				loadAdmission,
				loadRevisit,
				loadSchemeDiscount,
				loadDeposit,
				loadDrugRequest,
				loadLabRequest,
				loadRadiologyRequest );
	}

	@PostMapping( value = "patient-registration" )
	public PatientDetailDto registerPatient( @RequestBody PatientDetailDto patientDetailDto ) {
		return this.registrationService.patientRegistration( patientDetailDto );
	}

	@PutMapping( "patient-edit" )
	public ResponseDto<?> editPatient( @Validated @RequestBody PatientDetailDto patientDetailDto ) {
		return this.registrationService.patientRecordUpdate( patientDetailDto );
	}

	@GetMapping( "find-one/{patientId}" )
	public PatientDetailDto findPatientById( @PathVariable Long patientId ) {
		return this.registrationService.findPatientById( patientId );
	}

	@GetMapping( value = "get-nationality-by-lga/{nationalityId}" )
	public NationalityDto findNationalityAndLga( @PathVariable String nationalityId ) {
		return this.registrationService.getNationalityAndLga( Long.valueOf( nationalityId ) );
	}


	@PostMapping( value = "update-patient-scheme" )
	public ResponseEntity<?> updatePatientSchemeDetails( @RequestBody PatientDetailDto patient ) {
		return this.registrationService.updatePatientSchemeDetails( patient );
	}

	@PostMapping( value = "change-patient-category" )
	public ResponseEntity<?> updatePatientCategory( @RequestBody PatientCategoryUpdate payload ) {
		return this.registrationService.changePatientCategory( payload );
	}
}
