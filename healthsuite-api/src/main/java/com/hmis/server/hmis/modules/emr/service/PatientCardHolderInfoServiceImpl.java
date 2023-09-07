package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Relationship;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientCardHolderInfoService;
import com.hmis.server.hmis.modules.emr.dto.CardHolderTypeEnum;
import com.hmis.server.hmis.modules.emr.dto.PatientCardHolderInfoDto;
import com.hmis.server.hmis.modules.emr.model.PatientCardHolderInfo;
import com.hmis.server.hmis.modules.emr.repository.PatientCardHolderInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientCardHolderInfoServiceImpl implements IPatientCardHolderInfoService {

	private final PatientCardHolderInfoRepository repository;
	private final HmisUtilService utilService;

	@Autowired
	public PatientCardHolderInfoServiceImpl( PatientCardHolderInfoRepository repository, HmisUtilService utilService ) {
		this.repository = repository;
		this.utilService = utilService;
	}

	@Override
	public PatientCardHolderInfo createPatientCardHolderInfo( PatientCardHolderInfoDto dto ) {
		if ( dto.getCardHolderType() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Provide Card Holder Type" );
		}
		try {
			PatientCardHolderInfo cardHolderInfo = new PatientCardHolderInfo();
			this.setCardHolder( dto, cardHolderInfo );
			return this.repository.save( cardHolderInfo );
		} catch ( Exception e ) {
			throw new HmisApplicationException( e.getMessage() );
		}
	}

	private void setCardHolder( PatientCardHolderInfoDto dto, PatientCardHolderInfo cardHolderInfo ) {
		if ( dto.getName() != null ) {
			cardHolderInfo.setName( dto.getName() );
		}
		if ( dto.getCardExpiry() != null ) {
			cardHolderInfo.setCardExpiry( this.utilService.transformToLocalDate( dto.getCardExpiry() ) );
		}
		if ( dto.getInsuranceNumber() != null ) {
			cardHolderInfo.setInsuranceNumber( dto.getInsuranceNumber() );
		}
		if ( dto.getInsuranceNumber() != null ) {
			cardHolderInfo.setInsuranceNumber( dto.getInsuranceNumber() );
		}
		if ( dto.getDepartment() != null ) {
			cardHolderInfo.setDepartment( dto.getDepartment() );
		}
		if ( dto.getPlaceOfWork() != null ) {
			cardHolderInfo.setPlaceOfWork( dto.getPlaceOfWork() );
		}
		CardHolderTypeEnum cardHolderType = dto.getCardHolderType();
		if ( cardHolderType != null ) {
			cardHolderInfo.setCardHolderType( cardHolderType );
			if ( cardHolderType.equals( CardHolderTypeEnum.DEPENDANT ) ) {
				cardHolderInfo.setBeneficiaryName( dto.getBeneficiaryName() );
				cardHolderInfo.setRelationshipWithCardHolder( new Relationship( dto.getRelationShipWithCardHolder().getId().get() ) );
			}
		}
	}

	@Override
	public PatientCardHolderInfo updatePatientCardHolderInfo( PatientCardHolderInfoDto dto ) {
		try {
			if ( !dto.getId().isPresent() ) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Provide Card Holder Id" );
			}
			PatientCardHolderInfo cardHolderInfo = this.findPatientCardHolderInfo( dto.getId().get() );
			this.setCardHolder( dto, cardHolderInfo );
			return this.repository.save( cardHolderInfo );
		} catch ( Exception e ) {
			throw new HmisApplicationException( e.getMessage() );

		}
	}

	@Override
	public PatientCardHolderInfo findPatientCardHolderInfo( Long id ) {

		Optional<PatientCardHolderInfo> cardHolderInfo = this.repository.findById( id );
		if ( !cardHolderInfo.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Card Holder Not Found" );
		}
		return cardHolderInfo.get();
	}

	@Override
	public void removePatientCardHolderInfo( Long id ) {
		try {
			this.repository.deleteById( id );
		} catch ( Exception e ) {
			System.out.println( e.getMessage() );
		}
	}

}
