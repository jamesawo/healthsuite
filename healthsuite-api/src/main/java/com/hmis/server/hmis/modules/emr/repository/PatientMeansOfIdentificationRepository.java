package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientMeansOfIdentification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMeansOfIdentificationRepository extends JpaRepository<PatientMeansOfIdentification, Long> {
}
