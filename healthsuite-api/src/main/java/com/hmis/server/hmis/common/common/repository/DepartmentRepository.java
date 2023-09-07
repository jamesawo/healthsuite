package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository< Department, Long > {
	Optional< Department > findTopByOrderByIdDesc();

	List< Department > findDepartmentsByDepartmentCategory(DepartmentCategory departmentCategory);

	List< Department > findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase(String name, String code);

	List< Department > findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCaseAndDepartmentCategory(String name, String code, DepartmentCategory category);

	List< Department > findAllByNameContainsIgnoreCaseAndDepartmentCategory_Id(String name, Long departmentCatId);

	Department findByCode(String code);

	Optional<Department> findByName(String name);

	List< Department > findAllByNameContainsIgnoreCaseAndDepartmentCategory(String name, DepartmentCategory departmentCategory);

	List< Department > findAllByNameIsLikeIgnoreCase(String name);
}
