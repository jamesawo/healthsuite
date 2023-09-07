package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClerkRequestDrugRepository extends JpaRepository<ClerkRequestDrug, Long> {
    Optional<ClerkRequestDrug> findTopByOrderByIdDesc();
}
