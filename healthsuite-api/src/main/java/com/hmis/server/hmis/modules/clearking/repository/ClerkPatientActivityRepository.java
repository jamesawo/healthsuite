package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.dto.ClerkPatientActivityEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkPatientActivity;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ClerkPatientActivityRepository extends JpaRepository<ClerkPatientActivity, Long> {
    // Specific Records
    List<ClerkPatientActivity> findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualAndActivityEnumIn(
            PatientDetail patientDetail,
            LocalDate endDate,
            LocalDate startDate,
            Collection<ClerkPatientActivityEnum> activityEnum);

    List<ClerkPatientActivity> findAllByPatientAndActivityEnumIsInAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateDescTimeDesc(
            PatientDetail patient,
            List<ClerkPatientActivityEnum> activityEnum,
            LocalDate end, LocalDate start);


    List<ClerkPatientActivity> findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateDescTimeDesc(
            PatientDetail patientDetail, LocalDate date, LocalDate date2);

}
