package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientAppointment;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientAppointmentRepository extends JpaRepository< PatientAppointment, Long > {

	Optional< PatientAppointment > findByPatientDetailAndConsultantAndAppointmentStatusIgnoreCase( PatientDetail patientDetail, User consultant, String status );

	boolean existsByPatientDetailAndConsultantAndDateAndAppointmentStatus( PatientDetail patientDetail, User consultant, LocalDate date, String appointmentStatus );

	int countAllByConsultantAndAppointmentStatus( User consultant, String appointmentStatus );

	List< PatientAppointment > findAllByPatientDetailAndAppointmentStatus( PatientDetail patientDetail, String appointmentStatus );

	@Transactional
	@Modifying
	@Query( "UPDATE PatientAppointment a SET a.appointmentStatus = :appointmentStatus WHERE a.id  = :appointmentId " )
	int updateAppointmentStatus(
			@Param( "appointmentId" ) Long appointmentId,
			@Param( "appointmentStatus" ) String appointmentStatus );

}

