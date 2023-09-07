package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientNumberRepository extends JpaRepository<PatientNumber, Long> {
    PatientNumber findTopByOrderByIdDesc();
}
