package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findTopByOrderByIdDesc();

    Optional<Ward> findByCode(String code);

    Optional<Ward> findByDepartment(Department department);

    List<Ward> findAllByDepartmentNameContainsIgnoreCaseOrCodeContainsIgnoreCase(String name, String code);


}
