package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.model.PatientFluidBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientFluidBalanceRepository extends JpaRepository<PatientFluidBalance, Long> {
    List<PatientFluidBalance> findAllByPatientDetail(PatientDetail patientDetail);

//    @Query("select p from PatientFluidBalance p where p.patientDetail = :patient" )

    @Query(value = "SELECT d FROM PatientFluidBalance d where d.patientDetail = :detail group by d.date")
    public List<Object[]> findAllPatientFluidBalance(@Param(value = "detail") PatientDetail patientDetail);
}
