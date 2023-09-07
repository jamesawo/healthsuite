package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.pharmacy.model.DrugIssuance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugIssuanceRepository extends JpaRepository< DrugIssuance, Long > {
	Optional< DrugIssuance > findTopByOrderByIdDesc();

	Optional< DrugIssuance > findByDrugRequisitionId(Long drugRequisitionId);
}
