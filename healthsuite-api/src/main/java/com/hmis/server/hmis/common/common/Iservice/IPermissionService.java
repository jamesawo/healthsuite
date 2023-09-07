package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.PermissionDto;
import com.hmis.server.hmis.common.common.dto.PermissionsMapDto;
import com.hmis.server.hmis.common.common.model.Permission;

import java.util.List;
import java.util.Map;

public interface IPermissionService {
    PermissionDto createOne(PermissionDto permissionDto);

    List<PermissionDto> createInBatch(List<PermissionDto> permissionDtoList);

    List<PermissionDto> findAll();

//    Map<String, List<PermissionDto>> findAllGroupByModule();

    List<PermissionsMapDto> findAllGroupByModule();

    List<PermissionDto> findByRole(PermissionDto permissionDto);

    List<Permission> findByModule(String module);

    PermissionDto findOne(PermissionDto permissionDto);

    PermissionDto findByName(PermissionDto permissionDto);

    PermissionDto findByNameLike(PermissionDto permissionDto);

    PermissionDto updateOne(PermissionDto permissionDto);

    List<PermissionDto> updateInBatch(List<PermissionDto> permissionDtoList);
}
