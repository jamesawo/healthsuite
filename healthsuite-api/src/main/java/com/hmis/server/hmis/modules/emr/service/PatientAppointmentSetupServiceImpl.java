package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.IService.IPatientAppointmentSetupService;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentSetupDto;
import com.hmis.server.hmis.modules.emr.model.PatientAppointmentSetup;
import com.hmis.server.hmis.modules.emr.repository.PatientAppointmentSetupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientAppointmentSetupServiceImpl implements IPatientAppointmentSetupService {

	@Autowired
	private PatientAppointmentSetupRepository appointmentSetupRepository;


	@Override
	public PatientAppointmentSetupDto createAppointmentSetup(PatientAppointmentSetupDto dto) {
		try {
			PatientAppointmentSetup model = new PatientAppointmentSetup();
			this.mapToModel(dto, model);
			PatientAppointmentSetup save = this.appointmentSetupRepository.save(model);
			dto.setId(save.getId());
			return dto;
		}catch ( DataIntegrityViolationException e ){
			throw new ResponseStatusException( HttpStatus.CONFLICT, "Error! Consultant Duplicate Entry" );
		}catch ( Exception e ){
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public PatientAppointmentSetupDto updateAppointmentSetup( PatientAppointmentSetupDto dto ) {
		if ( dto.getId() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Provide Setup Before Updating" );
		}

		PatientAppointmentSetup setup = new PatientAppointmentSetup();
		this.mapToModel( dto,  setup);
		this.appointmentSetupRepository.save( setup );
		return dto;
	}

	@Override
	public int getConsultantLimitCount( Long consultantId ) {
		Optional< PatientAppointmentSetup > byConsultant =
				this.appointmentSetupRepository.findByConsultant( new User( consultantId ) );
		if ( ! byConsultant.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Consultant Setup Not Found" );
		}
		return byConsultant.get().getStaffLimit();
	}

	@Override
	public boolean existByConsultantAndSpeciality( Long consultantId, Long specialityId ) {
		return this.appointmentSetupRepository.existsByConsultantAndSpecialityUnit( new User( consultantId ),
																					new SpecialityUnit( specialityId ) );

	}

	private void mapToModel( PatientAppointmentSetupDto dto, PatientAppointmentSetup model ) {
		if (dto.getId() != null) {
			model.setId(dto.getId());
		}
		if (dto.getStaffLimit() > 0) {
			model.setStaffLimit(dto.getStaffLimit() );
		}
		if (dto.getConsultantId() != null ) {
			model.setConsultant(new User( dto.getConsultantId()) );
		}

		if ( dto.getSpecialityUnitId() != null ) {
			model.setSpecialityUnit( new SpecialityUnit(dto.getSpecialityUnitId()) );
		}
	}
}
