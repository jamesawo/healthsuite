package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
	List<Permission> findAllByModuleContainsIgnoreCase(String module);
}
