package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkPatientActivity;
import com.hmis.server.hmis.modules.clearking.model.ClerkPatientWardTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClerkPatientWardTransferRepository extends JpaRepository<ClerkPatientWardTransfer, Long> {
}
