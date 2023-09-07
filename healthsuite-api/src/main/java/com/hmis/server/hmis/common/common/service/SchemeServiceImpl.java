package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.ISchemeService;
import com.hmis.server.hmis.common.common.dto.SchemeDto;
import com.hmis.server.hmis.common.common.dto.SchemeOrganizationTypeEnum;
import com.hmis.server.hmis.common.common.dto.SchemePlanDto;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.model.SchemePlan;
import com.hmis.server.hmis.common.common.repository.SchemePlanRepository;
import com.hmis.server.hmis.common.common.repository.SchemeRepository;
import com.hmis.server.hmis.modules.billing.dto.SchemeBillDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SchemeServiceImpl implements ISchemeService {
	private final SchemeRepository schemeRepository;
	private final SchemePlanRepository schemePlanRepository;

	@Autowired
	public SchemeServiceImpl(
			SchemeRepository schemeRepository,
			SchemePlanRepository schemePlanRepository
	) {
		this.schemeRepository = schemeRepository;
		this.schemePlanRepository = schemePlanRepository;
	}

	@Override
	public Scheme createOne( SchemeDto schemeDto ) {
		Scheme scheme = new Scheme();
		this.setScheme( schemeDto, scheme );
		Scheme save = this.schemeRepository.save( scheme );
		this.saveSchemePlans( schemeDto.getSchemePlans(), save );
		return save;
	}

	@Override
	public List<SchemeDto> createInBatch( List<SchemeDto> schemeDtoList ) {
		return null;
	}

	@Override
	public List<Scheme> findAll() {
		return this.schemeRepository.findAll();
	}

	@Override
	public List<SchemeDto> findAllScheme() {
		List<SchemeDto> schemeDtoList = new ArrayList<>();
		List<Scheme> all = this.schemeRepository.findAll();
		if ( !all.isEmpty() ) {
			schemeDtoList = all.stream().map( Scheme::mapToDto ).collect( Collectors.toList() );
		}
		return schemeDtoList;
	}

	@Override
	public Scheme findById( Long id ) {
		Optional<Scheme> scheme = this.schemeRepository.findById( id );
		if ( !scheme.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Scheme Not Found" );
		}
		return scheme.get();
	}

	@Override
	public List<SchemeDto> findByNameLike( SchemeDto schemeDto ) {
		return null;
	}

	@Override
	public SchemeDto findByName( SchemeDto schemeDto ) {
		return null;
	}

	@Override
	public SchemeDto findByCode( SchemeDto schemeDto ) {
		return null;
	}

	@Override
	public Scheme updateOne( SchemeDto schemeDto ) {
		if ( schemeDto.getId() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "No Scheme Id Provided" );
		}
		Scheme scheme = this.findById( schemeDto.getId() );
		this.setScheme( schemeDto, scheme );
		return this.schemeRepository.save( scheme );
	}

	@Override
	public List<SchemeDto> updateInBatch( List<SchemeDto> schemeDtoList ) {
		return null;
	}

	@Override
	public void deactivateOne( SchemeDto schemeDto ) {

	}

	@Override
	public void deactivateInBatch( List<SchemeDto> schemeDtoList ) {

	}

	@Override
	public void activateOne( SchemeDto schemeDto ) {

	}

	@Override
	public void activateInBatch( List<SchemeDto> schemeDtoList ) {

	}

	@Override
	public List<SchemeDto> findByInsuranceNameOrCode( String search ) {
		List<SchemeDto> schemeDtoList = new ArrayList<>();
		List<Scheme> list = this.schemeRepository.findAllByInsuranceNameContainsIgnoreCaseOrInsuranceCodeContainsIgnoreCase( search, search );
		if ( list.size() > 0 ) {
			schemeDtoList = list.stream().map( Scheme::mapToDto ).collect( Collectors.toList() );
		}
		return schemeDtoList;
	}

	@Override
	public void addPatientBillToScheme( SchemeBillDto dto ) {

	}

	private void setScheme( SchemeDto dto, Scheme scheme ) {
		if ( dto.getId() != null )
			scheme.setId( dto.getId() );
		if ( dto.getInsuranceName() != null )
			scheme.setInsuranceName( dto.getInsuranceName() );
		if ( dto.getPhoneNumber() != null )
			scheme.setPhoneNumber( dto.getPhoneNumber() );
		if ( dto.getInsuranceCode() != null )
			scheme.setInsuranceCode( dto.getInsuranceCode() );
		if ( dto.getDiscount() > 0 )
			scheme.setDiscount( dto.getDiscount() );
		if ( dto.getPostalAddress() != null )
			scheme.setPostalAddress( dto.getPostalAddress() );
		if ( dto.getEmailAddress() != null )
			scheme.setEmailAddress( dto.getEmailAddress() );
		if ( dto.getAddressLineTitle() != null )
			scheme.setAddressLineTitle( dto.getAddressLineTitle() );
		if ( dto.getAddress1() != null )
			scheme.setAddress1( dto.getAddress1() );
		if ( dto.getAddress2() != null )
			scheme.setAddress2( dto.getAddress2() );
		if ( dto.getAddress3() != null )
			scheme.setAddress3( dto.getAddress3() );
		if ( dto.getAddress4() != null )
			scheme.setAddress4( dto.getAddress4() );
		if ( dto.getOrganizationType() != null )
			scheme.setOrganizationTypeEnum( SchemeOrganizationTypeEnum.valueOf( dto.getOrganizationType() ) );
	}

	private void saveSchemePlans( List<SchemePlanDto> schemePlans, Scheme scheme ) {
		if ( !schemePlans.isEmpty() ) {
			for ( SchemePlanDto dto : schemePlans ) {
				SchemePlan plan = new SchemePlan();
				plan.setId( dto.getId() != null ? dto.getId() : null );
				plan.setPlanName( dto.getPlanType() );
				plan.setDiscountMargin( dto.getDiscount() );
				plan.setDrugPercent( dto.getPercentDrug() );
				plan.setServicePercent( dto.getPercentService() );
				plan.setScheme( scheme );
				this.schemePlanRepository.save( plan );
			}
		}
	}

}
