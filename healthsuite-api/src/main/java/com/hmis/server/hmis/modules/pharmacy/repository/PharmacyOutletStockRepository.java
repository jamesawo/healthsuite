package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyOutletStock;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PharmacyOutletStockRepository extends JpaRepository< PharmacyOutletStock, Long > {

	Optional< PharmacyOutletStock > findByDrugRegisterAndDepartment(DrugRegister drugRegister, Department department);

	@Transactional()
	@Modifying( clearAutomatically = true, flushAutomatically = true )
	@Query( value = "update PharmacyOutletStock p set p.balance = p.balance - :quantity where p.department = :department and p.drugRegister = :drugRegister" )
	void subtractDrugRegisterFromOutlet(@Param( "department" ) Department department, @Param( "drugRegister" ) DrugRegister drugRegister, @Param( "quantity" ) int quantity);

	@Transactional
	@Modifying( clearAutomatically = true, flushAutomatically = true )
	@Query( value = "update PharmacyOutletStock p set p.balance = p.balance + :quantity where p.department = :department and p.drugRegister = :drugRegister" )
	void addDrugRegisterToOutletCount(@Param( "department" ) Department department, @Param( "drugRegister" ) DrugRegister drugRegister, @Param( "quantity" ) int quantity);
}
