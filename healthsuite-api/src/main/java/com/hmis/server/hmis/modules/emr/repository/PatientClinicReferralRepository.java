package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientClinicReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientClinicReferralRepository  extends JpaRepository<PatientClinicReferral, Long> {
}
