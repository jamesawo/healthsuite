package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.dto.GenderDto;
import com.hmis.server.hmis.modules.billing.dto.WalkInPatientDto;
import com.hmis.server.hmis.modules.billing.iservice.IWalkInPatientService;
import com.hmis.server.hmis.modules.billing.model.WalkInPatient;
import com.hmis.server.hmis.modules.billing.repository.WalkInPatientRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WalkInPatientServiceImpl implements IWalkInPatientService {

	@Autowired
	WalkInPatientRepository walkInPatientRepository;

	@Override
	public WalkInPatient findById( Long id ) {
		Optional< WalkInPatient > patient = this.walkInPatientRepository.findById(id);
		if( !patient.isPresent() ){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return patient.get();
	}

	@Override
	public WalkInPatient createWalkInPatient( WalkInPatientDto dto ){
		WalkInPatient walkInPatient = this.mapToModel( dto );
		return this.walkInPatientRepository.save( walkInPatient );
	}

	@Override
	public WalkInPatient searchWalkInPatient( String searchTerm ){
		return null;
	}

	public WalkInPatient mapToModel(WalkInPatientDto dto) {
		WalkInPatient walkInPatient = new WalkInPatient();
		if( dto.getFirstName() != null ){
			walkInPatient.setFirstName( dto.getFirstName() );
		}

		if( dto.getLastName() != null ){
			walkInPatient.setLastName( dto.getLastName() );
		}

		if( dto.getOtherName() != null ){
			walkInPatient.setOtherName( dto.getOtherName() );
		}

		if( dto.getAddress() != null ){
			walkInPatient.setAddress( dto.getAddress() );
		}

		if( dto.getPhone() != null ){
			walkInPatient.setPhone( dto.getPhone() );
		}

		if( dto.getAge() != null ){
			walkInPatient.setAge( dto.getAge() );
		}
		return walkInPatient;
	}

	public WalkInPatientDto mapToDto(WalkInPatient model) {
		WalkInPatientDto dto = new WalkInPatientDto();

		if( model.getId() != null ) {
			dto.setId(model.getId());
		}
		if( model.getFirstName() != null ) {
			dto.setFirstName(model.getFirstName());
		}
		if( model.getLastName() != null ) {
			dto.setLastName(model.getLastName());
		}
		if( model.getOtherName() != null ) {
			dto.setOtherName(model.getOtherName());
		}
		if( model.getAddress() != null ) {
			dto.setAddress(model.getAddress());
		}
		if( model.getPhone() != null ) {
			dto.setPhone(model.getPhone());
		}
		if( model.getAge() != null ) {
			dto.setAge(model.getAge());
		}
		if( model.getGender() != null ) {
			dto.setGender(new GenderDto(Optional.of(model.getGender().getId()), Optional.of(model.getGender().getName())));
			dto.setGenderId(model.getGender().getId());
		}
		return dto;
	}

}
