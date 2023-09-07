package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientVisit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientActiveVisitRepository extends JpaRepository< PatientVisit, Long > {

	Optional< PatientVisit > findByPatient(PatientDetail patientDetail);

	Optional< PatientVisit > findByPatient_Id(Long patientId);

}
