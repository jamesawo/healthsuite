package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.pharmacy.model.DrugOrder;
import com.hmis.server.hmis.modules.pharmacy.model.DrugOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DrugOrderItemRepository extends JpaRepository< DrugOrderItem, Long > {

	@Modifying
	@Transactional
	@Query( value = "delete from DrugOrderItem d where d.drugOrder = :order" )
	void deleteByDrugOrder(@Param( "order" ) DrugOrder order);
}
