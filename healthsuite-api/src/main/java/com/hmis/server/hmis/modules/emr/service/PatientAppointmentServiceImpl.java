package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.common.service.SpecialityUnitServiceImpl;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.emr.IService.IPatientAppointmentService;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentDto;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentStatusEnum;
import com.hmis.server.hmis.modules.emr.model.PatientAppointment;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.constant.HmisExceptionMessage.*;
import static com.hmis.server.hmis.modules.emr.dto.PatientAppointmentStatusEnum.OPEN;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PatientAppointmentServiceImpl implements IPatientAppointmentService {

	@Autowired
	private PatientAppointmentRepository repo;

	@Autowired
	private HmisUtilService utilService;

	@Autowired
	private PatientAppointmentSetupServiceImpl appointmentSetupService;

	@Autowired
	private DepartmentServiceImpl departmentService;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private SpecialityUnitServiceImpl specialityUnitService;

	@Override
	public PatientAppointmentDto createAppointment( PatientAppointmentDto dto ) {

		this.validateBookingRequest( dto );
		PatientAppointment model = new PatientAppointment();
		this.mapToModel( dto, model );
		PatientAppointment appointment = this.repo.save( model );
		dto.setId( appointment.getId() );
		return dto;
	}

	@Override
	public PatientAppointmentDto updateAppointment( PatientAppointmentDto dto ) {
		return null;
	}

	@Override
	public PatientAppointmentStatusEnum checkAppointmentStatus( Long id ) {
		PatientAppointment appointment = this.findPatientAppointment( id );
		return PatientAppointmentStatusEnum.valueOf( appointment.getAppointmentStatus() );
	}

	@Override
	public PatientAppointment findPatientAppointment( Long id ) throws ResponseStatusException {
		Optional< PatientAppointment > optional = this.repo.findById( id );
		if ( ! optional.isPresent() ) {
			throw new ResponseStatusException( NOT_FOUND, NOTHING_FOUND );
		}
		return optional.get();
	}

	@Override
	public Optional< PatientAppointment > findByPatientAndConsultantAndStatus( Long patientId, Long consultantId, PatientAppointmentStatusEnum statusEnum ) {
		return this.repo.findByPatientDetailAndConsultantAndAppointmentStatusIgnoreCase( new PatientDetail( patientId ), new User( consultantId ), statusEnum.label );
	}

	@Override
	public boolean isSameAppointmentExist( Long patientId, Long consultantId, LocalDate date, String status ) {
		return this.repo.existsByPatientDetailAndConsultantAndDateAndAppointmentStatus( new PatientDetail( patientId ), new User( consultantId ), date, status );
	}

	@Override
	public void validateBookingRequest( PatientAppointmentDto appointmentDto ) {
		Long consultantId = appointmentDto.getConsultantId();
		Long specialityId = appointmentDto.getSpecialityId();
		Long patientId = appointmentDto.getPatientId();
		LocalDate date = this.utilService.transformToLocalDate( appointmentDto.getDateTime() );

		/* check if the consultant is assigned to speciality unit */
		boolean exitsByConsultantAndSpeciality =
				this.appointmentSetupService.existByConsultantAndSpeciality( consultantId, specialityId );

		if ( ! exitsByConsultantAndSpeciality ) {
			throw new ResponseStatusException( CONFLICT, CONSULTANT_MISMATCH_SPECIALITY );
		}

		/* check appointment duplicate status */
		boolean alreadyExist =
				this.isSameAppointmentExist( patientId, consultantId, date, OPEN.label );

		if ( alreadyExist ) {
			throw new ResponseStatusException( CONFLICT, DUPLICATE_APPOINTMENT );
		}

		/* check if consultant limit is reached  */
		boolean isConsultantLimit = this.isConsultantLimitReached( consultantId );
		if ( isConsultantLimit ) {
			throw new ResponseStatusException( CONFLICT, CONSULTANT_LIMIT_REACHED );
		}

	}

	@Override
	public boolean isConsultantLimitReached( Long consultantId ) {
		int limitSetup = this.appointmentSetupService.getConsultantLimitCount( consultantId );
		int currentLimit =
				this.repo.countAllByConsultantAndAppointmentStatus( new User( consultantId ), OPEN.label );

		return currentLimit >= limitSetup;
	}

	@Override
	public List< PatientAppointmentDto > findAllPatientOpenAppointment( Long patientId ) {
		List< PatientAppointmentDto > appointmentDos = new ArrayList<>();
		List< PatientAppointment > list =
				this.repo.findAllByPatientDetailAndAppointmentStatus( new PatientDetail( patientId ), OPEN.label );
		if ( list.size() > 0 ) {
			for ( PatientAppointment appointment : list ) {
				PatientAppointmentDto dto = new PatientAppointmentDto();
				this.mapToDto( dto, appointment );
				appointmentDos.add( dto );
			}
		}
		return appointmentDos;
	}

	@Override
	public boolean cancelPatientAppointment( Long appointmentId ) {
		try {
			int result =
					this.repo.updateAppointmentStatus( appointmentId, PatientAppointmentStatusEnum.CLOSED.label );
			return result > 0;
		}
		catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}

	}


	private void mapToModel( PatientAppointmentDto dto, PatientAppointment model ) {
		if ( dto.getId() != null ) {
			model.setId( dto.getId() );
		}

		if ( dto.getPatientId() != null ) {
			model.setPatientDetail( new PatientDetail( dto.getPatientId() ) );
		}

		if ( dto.getDateTime() != null ) {
			model.setDate( this.utilService.transformToLocalDate( dto.getDateTime() ) );
			model.setTime( this.utilService.transformToLocalTime( dto.getDateTime() ) );
		}

		if ( dto.getBookedById() != null ) {
			model.setBookedBy( new User( dto.getBookedById() ) );
		}

		if ( dto.getSpecialityId() != null ) {
			model.setSpecialityUnit( new SpecialityUnit( dto.getSpecialityId() ) );
		}

		if ( dto.getAppointmentStatus() != null ) {
			model.setAppointmentStatus( dto.getAppointmentStatus().label );
		}

		if ( dto.getLocationId() != null ) {
			model.setLocation( new Department( dto.getLocationId() ) );
		}

		if ( dto.getRemarks() != null ) {
			model.setRemarks( dto.getRemarks() );
		}

		if ( dto.getConsultantId() != null ) {
			model.setConsultant( new User( dto.getConsultantId() ) );
		}

		if ( dto.getClinicId() != null ) {
			model.setWard( new Department( dto.getClinicId() ) );
		}
	}


	private void mapToDto( PatientAppointmentDto data, PatientAppointment model ) {
		if ( model.getId() != null ) {
			data.setId( model.getId() );
		}

		if ( model.getPatientDetail() != null ) {
			data.setPatientId( model.getPatientDetail().getId() );
		}

		if ( model.getDate() != null && model.getTime() != null ) {
			data.setDateTime( this.utilService.transformDateAndTime( model.getDate(), model.getTime() ) );
		}


		if ( model.getBookedBy() != null ) {
			data.setBookedById( model.getBookedBy().getId() );
		}

		if ( model.getSpecialityUnit() != null ) {
			data.setSpecialityId( model.getSpecialityUnit().getId() );
			data.setSpecialityUnit( this.specialityUnitService.mapModelToDto( model.getSpecialityUnit() ) );
		}

		if ( model.getAppointmentStatus() != null ) {
			data.setAppointmentStatus( PatientAppointmentStatusEnum.valueOf( model.getAppointmentStatus() ) );
		}

		if ( model.getLocation() != null ) {
			data.setLocationId( model.getLocation().getId() );
		}

		if ( model.getRemarks() != null ) {
			data.setRemarks( model.getRemarks() );
		}

		if ( model.getConsultant() != null ) {
			data.setConsultantId( model.getConsultant().getId() );
			data.setConsultant( this.userService.setConsultantBasicDetails( model.getConsultant() ) );
		}

		if ( model.getWard() != null ) {
			data.setClinicId( model.getWard().getId() );
			data.setClinic( this.departmentService.mapModelToDto( model.getWard() ) );
		}
	}


}
