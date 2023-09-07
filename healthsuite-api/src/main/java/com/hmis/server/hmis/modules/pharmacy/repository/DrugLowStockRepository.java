package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.model.DrugLowStock;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugLowStockRepository extends JpaRepository< DrugLowStock, Long> {
	Optional<DrugLowStock> findByOutletEqualsAndDrugEquals(Department outlet, DrugRegister drug);

	void deleteByOutletEqualsAndDrugEquals(Department outlet, DrugRegister drugRegister);
}
