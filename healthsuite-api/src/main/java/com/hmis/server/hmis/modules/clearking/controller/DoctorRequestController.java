package com.hmis.server.hmis.modules.clearking.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.clearking.dto.ClerkDoctorRequestDto;
import com.hmis.server.hmis.modules.clearking.dto.DoctorRequestSearchDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrug;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestLab;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestRadiology;
import com.hmis.server.hmis.modules.clearking.service.DoctorRequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/doctor-request" )
public class DoctorRequestController {
	@Autowired
	private DoctorRequestServiceImpl requestService;

	@PostMapping( value = "/find-drug-request-by-date-range" )
	public List<ClerkRequestDrug> findDrugRequest( @RequestBody DoctorRequestSearchDto dto ) {
		try {
			return this.requestService.searchDrugRequestByDateRange( dto.getPatientId(), dto.getStartDate(), dto.getEndDate() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}


	@PostMapping( value = "/find-lab-request-by-date-range" )
	public List<ClerkRequestLab> findLabRequest( @RequestBody DoctorRequestSearchDto dto ) {
		try {
			return this.requestService.searchLabRequestByDataRange( dto.getPatientId(), dto.getStartDate(), dto.getEndDate() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	@PostMapping( value = "/find-radiology-request-by-date-range" )
	public List<ClerkRequestRadiology> findRadiologyRequest( @RequestBody DoctorRequestSearchDto dto ) {
		try {
			return this.requestService.searchRadiologyRequestByDataRange( dto.getPatientId(), dto.getStartDate(), dto.getEndDate() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	@PostMapping( value = "/save-doc-request" )
	public ResponseEntity<MessageDto> saveDoctorDrugRequest( @RequestBody ClerkDoctorRequestDto dto ) {
		return this.requestService.saveDrugRequest( dto );
	}

}
