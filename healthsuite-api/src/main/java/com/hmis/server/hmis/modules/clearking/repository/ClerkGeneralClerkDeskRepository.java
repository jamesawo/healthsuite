package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkGeneralClerkDesk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClerkGeneralClerkDeskRepository extends JpaRepository<ClerkGeneralClerkDesk, Long> {
}
