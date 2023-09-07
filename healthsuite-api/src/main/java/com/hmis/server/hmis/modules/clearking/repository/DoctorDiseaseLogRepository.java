package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkDoctorDiseaseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorDiseaseLogRepository extends JpaRepository<ClerkDoctorDiseaseLog, Long> {
}
