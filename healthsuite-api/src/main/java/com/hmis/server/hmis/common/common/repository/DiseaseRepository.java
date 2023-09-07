package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Optional<Disease> findTopByOrderByIdDesc();
}
