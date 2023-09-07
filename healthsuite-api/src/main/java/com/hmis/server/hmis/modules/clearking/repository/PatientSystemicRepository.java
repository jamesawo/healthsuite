package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkSystemicExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientSystemicRepository extends JpaRepository<ClerkSystemicExamination, Long> {
}
