package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientOtherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientOtherDetailRepository extends JpaRepository<PatientOtherDetail, Long> {
}
