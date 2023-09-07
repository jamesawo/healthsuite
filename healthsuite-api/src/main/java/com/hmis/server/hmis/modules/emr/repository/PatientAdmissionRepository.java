package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionStatusEnum;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientAdmissionRepository extends JpaRepository<PatientAdmission, Long> {
	Optional<PatientAdmission> findTopByOrderByIdDesc();
	Optional<PatientAdmission> findByPatientAndAdmissionStatusEquals(PatientDetail patient, PatientAdmissionStatusEnum admissionStatus);
	Optional<PatientAdmission> findByCode(String code);
	List<PatientAdmission> findAllByPatientAndAdmittedDateIsLessThanEqualAndAdmittedDateIsGreaterThanEqual(PatientDetail patient, LocalDate endDate, LocalDate startDate);
}
