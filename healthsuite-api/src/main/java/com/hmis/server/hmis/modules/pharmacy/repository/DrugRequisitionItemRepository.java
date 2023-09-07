package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisitionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DrugRequisitionItemRepository extends JpaRepository< DrugRequisitionItem, Long> {
	@Transactional
	@Modifying
	@Query( value = "update  DrugRequisitionItem d set d.issuingQuantity = :quantity where d.id = :id" )
	void updateRequisitionItemIssuingQty(@Param( value = "quantity" ) Integer quantity, @Param( value = "id" ) Long id);
}
