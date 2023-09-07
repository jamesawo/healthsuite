package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkRequestRadiologyItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClerkRequestRadiologyItemsRepository extends JpaRepository<ClerkRequestRadiologyItems, Long> {
}
