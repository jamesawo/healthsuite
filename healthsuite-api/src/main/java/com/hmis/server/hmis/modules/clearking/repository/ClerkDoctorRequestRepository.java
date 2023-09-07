package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkDoctorRequest;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClerkDoctorRequestRepository extends JpaRepository<ClerkDoctorRequest, Long> {

    @Query( value = " from ClerkDoctorRequest d where d.patient = :patient AND d.date BETWEEN :startDate AND :endDate " )
    List<ClerkDoctorRequest> findRecordByPatientAndDate(@Param("patient") PatientDetail patient,
                                                        @Param( "startDate" ) LocalDate startDate,
                                                        @Param( "endDate" ) LocalDate endDate);

    List<ClerkDoctorRequest> findAllByPatientAndDateIsBetween(PatientDetail patient, LocalDate date, LocalDate date2);

    List<ClerkDoctorRequest> findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateAsc(PatientDetail patient, LocalDate endDate, LocalDate startDate);
}
