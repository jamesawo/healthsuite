package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentDto;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentStatusEnum;
import com.hmis.server.hmis.modules.emr.model.PatientAppointment;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Validated
public interface IPatientAppointmentService {

	PatientAppointmentDto createAppointment( PatientAppointmentDto dto );

	PatientAppointmentDto updateAppointment( PatientAppointmentDto dto );

	PatientAppointmentStatusEnum checkAppointmentStatus( Long id );

	PatientAppointment findPatientAppointment( Long id );

	Optional< PatientAppointment > findByPatientAndConsultantAndStatus( Long patientId, Long consultantId, PatientAppointmentStatusEnum statusEnum );

	boolean isSameAppointmentExist( Long patientId, Long consultantId, LocalDate date, String status );

	void validateBookingRequest( PatientAppointmentDto appointmentDto );

	boolean isConsultantLimitReached( Long consultantId );

	List< PatientAppointmentDto > findAllPatientOpenAppointment( Long patientId );

	boolean cancelPatientAppointment( Long appontmentId );
}
