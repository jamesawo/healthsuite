package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.model.SchemePlan;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientInsuranceDetailService;
import com.hmis.server.hmis.modules.emr.dto.PatientInsuranceDetailDto;
import com.hmis.server.hmis.modules.emr.dto.SchemeTreatmentTypeEnum;
import com.hmis.server.hmis.modules.emr.model.PatientInsuranceDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientInsuranceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientInsuranceDetailServiceImpl implements IPatientInsuranceDetailService {
	private final PatientInsuranceDetailRepository repository;
	private final HmisUtilService utilService;

	@Autowired
	public PatientInsuranceDetailServiceImpl(
			PatientInsuranceDetailRepository repository,
			@Lazy HmisUtilService utilService ) {
		this.repository = repository;
		this.utilService = utilService;
	}

	@Override
	public PatientInsuranceDetail createPatientInsuranceDetail( PatientInsuranceDetailDto dto ) {
		try {
			PatientInsuranceDetail insuranceDetail = new PatientInsuranceDetail();
			this.setInsuranceDetails( dto, insuranceDetail );
			return this.repository.save( insuranceDetail );
		} catch ( Exception e ) {
			throw new HmisApplicationException( e.getMessage() );
		}
	}

	@Override
	public PatientInsuranceDetail updatePatientInsuranceDetail( PatientInsuranceDetailDto dto ) {
		if ( dto.getId() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST,
			                                   HmisExceptionMessage.ID_NOT_PROVIDED + "Patient Insurance"
			);
		}
		try {
			PatientInsuranceDetail insuranceDetail = this.findPatientInsuranceDetail( dto.getId() );
			this.setInsuranceDetails( dto, insuranceDetail );
			return this.repository.save( insuranceDetail );
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new HmisApplicationException( e.getMessage() );
		}
	}

	@Override
	public PatientInsuranceDetail findPatientInsuranceDetail( Long id ) {
		Optional<PatientInsuranceDetail> detail = this.repository.findById( id );
		if ( !detail.isPresent() ) {
			throw new HmisApplicationException( HmisExceptionMessage.NOTHING_FOUND + "patient insurance" );
		}
		return detail.get();
	}

	@Override
	public void removePatientInsuranceDetail( Long id ) {
		try {
			this.repository.deleteById( id );
		} catch ( Exception e ) {
			System.out.println( e.getMessage() );
		}
	}

	private void setInsuranceDetails( PatientInsuranceDetailDto dto, PatientInsuranceDetail detail ) {
		if ( dto.getId() != null ) {
			detail.setId( dto.getId() );
		}
		if ( dto.getScheme() != null && dto.getScheme().getId() != null && dto.getSchemePlanId() != null ) {
			detail.setScheme( new Scheme( dto.getScheme().getId() ) );
			detail.setSchemePlan( new SchemePlan( dto.getSchemePlanId() ) );
		}
		if ( dto.getPrimaryProvider() != null ) {
			detail.setPrimaryProvider( dto.getPrimaryProvider() );
		}
		if ( dto.getTypeOfCare() != null ) {
			detail.setTypeOfCare( dto.getTypeOfCare() );
		}
		if ( dto.getApprovalCode() != null ) {
			detail.setApprovalCode( dto.getApprovalCode() );
		}
		if ( dto.getDiagnosis() != null ) {
			detail.setDiagnosis( dto.getDiagnosis() );
		}
		if ( dto.getApprovalStartDate() != null ) {
			detail.setApprovalStartDate( this.utilService.transformToLocalDate( dto.getApprovalStartDate() ) );
		}
		if ( dto.getApprovalEndDate() != null ) {
			detail.setApprovalEndDate( this.utilService.transformToLocalDate( dto.getApprovalEndDate() ) );
		}
		if ( dto.getNhisNumber() != null ) {
			detail.setNhisNumber( dto.getNhisNumber() );
		}
		if ( dto.getTreatmentType() != null ) {
			detail.setTreatmentType( SchemeTreatmentTypeEnum.valueOf( dto.getTreatmentType() ) );
		}

	}
}
