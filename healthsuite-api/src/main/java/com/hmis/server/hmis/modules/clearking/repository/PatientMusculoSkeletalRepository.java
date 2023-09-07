package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkMusculoSkeletalExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMusculoSkeletalRepository extends JpaRepository<ClerkMusculoSkeletalExamination, Long> {
}
