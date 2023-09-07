package com.hmis.server.hmis.modules.emr.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentDto;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentSetupDto;
import com.hmis.server.hmis.modules.emr.service.PatientServiceImpl;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/emr" )
public class PatientAppointmentController {

	@Autowired
	PatientServiceImpl service;

	@PostMapping( "/patient-appointment-booking-setup" )
	public PatientAppointmentSetupDto createSetup(@Valid @RequestBody PatientAppointmentSetupDto dto) {
		return this.service.createAppointmentSetup(dto);
	}

	@PostMapping( "/patient-appointment-booking" )
	public PatientAppointmentDto createAppointmentBooking(@Valid @RequestBody PatientAppointmentDto dto) {

		return this.service.createBooking(dto);
	}

	@GetMapping( "/patient-open-appointments/{patientId}" )
	public List< PatientAppointmentDto > findPatientAppointment(@NotNull @PathVariable Long patientId) {
		return this.service.findPatientOpenAppointment(patientId);
	}

	@PutMapping( "/cancel-patient-appointment" )
	public ResponseDto< Boolean > cancelPatientAppointment(@RequestBody Long appointmentId) {
		return this.service.cancelPatientAppointment(appointmentId);
	}

}
