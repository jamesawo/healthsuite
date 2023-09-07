package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.model.PatientVitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientVitalSignServiceRepository extends JpaRepository<PatientVitalSign, Long> {
    PatientVitalSign findTopByOrderByIdDesc();

    PatientVitalSign findTopByPatientOrderByIdDesc(PatientDetail patient);

    List<PatientVitalSign> findAllByDateIsLessThanEqualAndDateGreaterThanEqualAndPatientOrderByDateDesc(LocalDate endDate, LocalDate startDate, PatientDetail patient);

    List<PatientVitalSign> findTop5ByDateIsLessThanEqualAndDateGreaterThanEqualAndPatientOrderByDateDesc(LocalDate endDate, LocalDate startDate, PatientDetail patient);
}
