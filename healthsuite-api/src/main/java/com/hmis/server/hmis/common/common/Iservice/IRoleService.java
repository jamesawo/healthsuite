package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.PermissionDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.model.Role;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface IRoleService {
    RoleDto createOne(RoleDto roleDto);

    List<RoleDto> createInBatch(List<RoleDto> roleDtoList);

    List<RoleDto> findAll();

    List<PermissionDto> findRolePermissions(RoleDto roleDto);

    List<RoleDto> findByNameLike(RoleDto roleDto);

    RoleDto findByName(RoleDto roleDto);

    RoleDto findOne(RoleDto roleDto);

    RoleDto updateOneWithPermissions(RoleDto roleDto);

    RoleDto updateOneWithoutPermissions(RoleDto roleDto);

    void deactivateRole(RoleDto roleDto);

    void activateRole(RoleDto roleDto);

    void deactivateInBatch(List<RoleDto> roleDtoList);

    void activateInBatch(List<RoleDto> roleDtoList);

    boolean isRoleExist(RoleDto roleDto);

    Role findOneRaw(long id);

    List<Role> findAllRaw();

    HashSet<Role> findAllByPermissionIn(List<Permission> permissions);

    boolean rolesHasAnyPermission(List<Role> roles, List<Permission> permissions);

    boolean hasAnyConsultantPermission(Collection<Role> roles);
}
