package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrug;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestLab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClerkRequestLabRepository extends JpaRepository<ClerkRequestLab, Long> {
}
