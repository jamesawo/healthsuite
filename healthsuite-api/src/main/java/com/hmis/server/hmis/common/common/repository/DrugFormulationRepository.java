package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.DrugFormulation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugFormulationRepository extends JpaRepository<DrugFormulation, Long> {
	Optional< DrugFormulation > findByNameIgnoreCase(String name);
}
