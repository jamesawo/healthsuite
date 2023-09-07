package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.SpecialityUnitServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.PatientAntenatalDto;
import com.hmis.server.hmis.modules.nurse.iservice.IPatientAntenatalBookingService;
import com.hmis.server.hmis.modules.nurse.model.PatientAntenatalBooking;
import com.hmis.server.hmis.modules.nurse.repository.PatientAntenatalBookingRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Slf4j
public class PatientAntenatalBookingServiceImpl implements IPatientAntenatalBookingService {

	@Autowired
	private PatientAntenatalBookingRepository antenatalBookingRepository;
	@Autowired
	private DepartmentServiceImpl departmentService;
	@Autowired
	private PatientDetailServiceImpl patientDetailService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SpecialityUnitServiceImpl specialityUnitService;

	public ResponseDto saveBooking(PatientAntenatalDto dto) {
		this.onValidateBeforeSave(dto);
		try {
			PatientAntenatalBooking booking = this.mapToModel(dto);
			Optional< PatientAntenatalBooking > byPatient = this.antenatalBookingRepository.findByPatient(booking.getPatient());
			if( byPatient.isPresent() ){
				this.updateOne(byPatient.get().getId(), booking);
				return new ResponseDto(HmisConstant.UPDATED_MESSAGE);
			}else{
				this.createOne(booking);
				return new ResponseDto(HmisConstant.SUCCESS_MESSAGE);
			}
		}
		catch( Exception e ) {
			log.error(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


	public void createOne(PatientAntenatalBooking booking){
		this.antenatalBookingRepository.save(booking);
	}

	public void updateOne(Long id, PatientAntenatalBooking booking){
		PatientAntenatalBooking anc = this.findOneRaw(id);
		booking.setId(anc.getId());
		this.antenatalBookingRepository.save(booking);
	}

	public PatientAntenatalBooking findOneRaw(Long id){
		Optional< PatientAntenatalBooking > optional = this.antenatalBookingRepository.findById(id);
		if( !optional.isPresent() ){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ANC Booking Not Found");
		}
		return optional.get();
	}

	public PatientAntenatalDto findOne(Long id){
		return null;
	}

	public PatientAntenatalDto findOneByPatient(Long patientId){
		Optional< PatientAntenatalBooking > byPatient = this.antenatalBookingRepository.findByPatient(new PatientDetail(patientId));
		if( !byPatient.isPresent() ){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "No Booking Found For Patient");
		}
		return this.mapToDto(byPatient.get());
	}

	private PatientAntenatalDto mapToDto(PatientAntenatalBooking model){
		PatientAntenatalDto dto = new PatientAntenatalDto();

		return dto;
	}

	private PatientAntenatalBooking mapToModel(PatientAntenatalDto dto) {
		PatientAntenatalBooking model = new PatientAntenatalBooking();
		if( ObjectUtils.isNotEmpty(dto.getPatient()) ) {
			model.setPatient(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
		}
		if( ObjectUtils.isNotEmpty(dto.getBookedBy()) ) {
			model.setBookedBy(this.userService.findOneRaw(dto.getBookedBy().getId().get()));
		}
		if( ObjectUtils.isNotEmpty(dto.getBookedFrom()) ) {
			model.setCaptureFromLocation(this.departmentService.findOneRaw(dto.getBookedFrom().getId()));
		}
		if( ObjectUtils.isNotEmpty(dto.getCaseConsultant()) ) {
			model.setCaseConsultant(this.userService.findOneRaw(dto.getCaseConsultant().getId().get()));
		}
		if( ObjectUtils.isNotEmpty(dto.getSpecialityUnit()) ) {
			model.setSpecialityUnit(this.specialityUnitService.findOneRaw(dto.getSpecialityUnit().getId().get()));
		}
		if( ObjectUtils.isNotEmpty(dto.getClinic()) ) {
			model.setClinic(this.departmentService.findOneRaw(dto.getClinic().getId()));
		}
		if( ObjectUtils.isNotEmpty(dto.getSpouseName()) ) {
			model.setSpouseName(dto.getSpouseName());
		}
		if( ObjectUtils.isNotEmpty(dto.getSpousePhone()) ) {
			model.setSpouseName(dto.getSpousePhone());
		}
		if( ObjectUtils.isNotEmpty(dto.getSpouseEmployer()) ) {
			model.setSpouseName(dto.getSpouseEmployer());
		}
		if( ObjectUtils.isNotEmpty(dto.getSpouseOccupation()) ) {
			model.setSpouseName(dto.getSpouseOccupation());
		}
		return model;
	}

	private void onValidateBeforeSave(PatientAntenatalDto dto) {
		if( ObjectUtils.isEmpty(dto.getPatient()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is Required");
		}
		if( ObjectUtils.isEmpty(dto.getClinic()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic is Required");
		}
		if( ObjectUtils.isEmpty(dto.getBookedFrom()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is Required");
		}
		if( ObjectUtils.isEmpty(dto.getBookedBy()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operating Nurse/User is Required");
		}
	}
}
