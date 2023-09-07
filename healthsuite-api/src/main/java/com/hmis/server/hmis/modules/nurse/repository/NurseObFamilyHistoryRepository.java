package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.nurse.model.NurseObFamilyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseObFamilyHistoryRepository extends JpaRepository<NurseObFamilyHistory, Long> {
}
