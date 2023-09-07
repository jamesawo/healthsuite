package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.model.PatientAntenatalBooking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientAntenatalBookingRepository extends JpaRepository< PatientAntenatalBooking, Long > {
	Optional< PatientAntenatalBooking > findByPatient(PatientDetail patientDetail);
}
