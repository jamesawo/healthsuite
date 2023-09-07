package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkRequestLabItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClerkRequestLabItemsRepository extends JpaRepository<ClerkRequestLabItems, Long> {
}
