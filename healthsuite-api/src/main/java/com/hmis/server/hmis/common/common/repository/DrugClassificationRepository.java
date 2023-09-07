package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.DrugClassification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugClassificationRepository extends JpaRepository<DrugClassification, Long> {
	Optional< DrugClassification > findByNameIgnoreCase(String name);

	Optional< DrugClassification > findByNameIgnoreCaseOrCodeIgnoreCase(String name, String code);
}
