package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentCategoryRepository extends JpaRepository<DepartmentCategory, Long> {

    Optional<DepartmentCategory> findByNameIsLikeIgnoreCase(String name);

    Optional<DepartmentCategory> findByName(String name);

    Optional<DepartmentCategory> findByNameIgnoreCase(String name);



}
