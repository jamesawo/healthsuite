package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueDepartmentRepository extends JpaRepository<RevenueDepartment, Long> {
    Optional<RevenueDepartment> findTopByOrderByIdDesc();

    List<RevenueDepartment>  findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase(String name, String code);

    RevenueDepartment findByCode(String code);

	List< RevenueDepartment > findByHandleDepositIsTrue();
}
