package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.model.DrugOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DrugOrderRepository extends JpaRepository< DrugOrder, Long > {

	Optional<DrugOrder> findTopByOrderByIdDesc();

	List<DrugOrder> findAllByCodeIsContainingIgnoreCase(String code);

	boolean existsByDepartmentAndStatusIsTrue(Department department);

	boolean existsByDepartmentAndFulfilledIsFalse(Department department);

	List< DrugOrder > findAllByDepartmentAndStatusIsFalse(Department department);

	@Transactional
	@Modifying
	@Query( value = "update DrugOrder d set d.fulfilled = :status where d.id = :id" )
	void setDrugOrderIsFulfilled(@Param( "status" ) boolean status, @Param( "id" ) Long id);

}
