package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.model.Role;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	HashSet<Role> findAllByPermissionsIn(List<Permission> permissions);

	Optional< Role > findByNameIgnoreCase(String name);

	@Query(value = "select r from Role r where r.name <> :name")
	List<Role> findAllWithoutSuperAdmin(@Param("name") String name);
}
