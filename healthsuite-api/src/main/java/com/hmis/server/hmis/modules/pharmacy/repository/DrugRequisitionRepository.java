package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisition;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DrugRequisitionRepository extends JpaRepository< DrugRequisition, Long> {
	Optional<DrugRequisition> findTopByOrderByIdDesc();

	List< DrugRequisition > findAllByIssuingDepartmentAndCreatedDateBetween(Department issuingDepartment, Date createdDate, Date createdDate2);

	@Query( value = " from DrugRequisition d where d.issuingDepartment = :department AND d.requestDate BETWEEN :startDate AND :endDate " )
	List< DrugRequisition > findRecordByDepartmentAndDate(@Param( "department" ) Department department, @Param( "startDate" ) LocalDate startDate, @Param( "endDate" ) LocalDate endDate);
	
	@Transactional
	@Modifying
	@Query( value = " update DrugRequisition d set d.isFulfilled = :status where d.id = :id" )
	void updateRequisitionIsFulfilled(@Param( value = "status" ) boolean status, @Param( value = "id" ) Long id);

}
