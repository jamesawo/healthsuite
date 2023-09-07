package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.modules.others.dto.ServiceColumnEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductServiceRepository extends JpaRepository<ProductService, Long> {

	Optional<ProductService> findTopByOrderByIdDesc();

	List<ProductService> findAllByNameIsLike( String name );

	List<ProductService> findAllByRevenueDepartment( RevenueDepartment revenueDepartment );

	List<ProductService> findAllByDepartment( Department department );

	List<ProductService> findAllByDepartmentOrderByIdAsc( Department department );

	List<ProductService> findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase( String name, String code );

	List<ProductService> findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCaseAndDepartment_DepartmentCategory_Id( String name, String code, Long id );

	List<ProductService> findAllByNameContainsIgnoreCaseAndDepartment_DepartmentCategory( String name, DepartmentCategory departmentCategory );

	Optional<ProductService> findByNameAndDepartmentAndRevenueDepartment( String name, Department department, RevenueDepartment revenueDepartment );

	@Modifying
	@Query( value = "UPDATE hmis_service_data s set :columnName =:columnValue where s.id = :id",
			nativeQuery = true )
	void updateServiceByColumn( @Param( "columnName" ) ServiceColumnEnum columnName, @Param( "columnValue" ) String columnValue, @Param( "id" ) Long id );

	List<ProductService> findAllByRevenueDepartmentAndDepartment( RevenueDepartment revenueDepartment, Department department );
}
