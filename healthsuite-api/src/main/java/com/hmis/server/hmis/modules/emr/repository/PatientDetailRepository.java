package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientDetailRepository extends JpaRepository<PatientDetail, Long>, JpaSpecificationExecutor<PatientDetail> {

    Optional<PatientDetail> findByPatientNumber(String patientNumber);

    List<PatientDetail> findAllByIdNotIn(List<Long> IdList);

    /*@Transactional
    @Modifying
    @Query(value = "UPDATE PatientDetail SET revisitStatus = :status where id = :id")
    void updatePatientRevisitStatus(@Param("status") boolean status, @Param("id")Long id);*/
}
