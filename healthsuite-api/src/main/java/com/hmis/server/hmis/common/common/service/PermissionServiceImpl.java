package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IPermissionService;
import com.hmis.server.hmis.common.common.dto.PermissionDto;
import com.hmis.server.hmis.common.common.dto.PermissionsMapDto;
import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public PermissionDto createOne(PermissionDto permissionDto) {
        return null;
    }

    @Override
    public List<PermissionDto> createInBatch(List<PermissionDto> permissionDtoList) {
        return null;
    }

    @Override
    public List<PermissionDto> findAll() {
        return mapModelListToDtoList(permissionRepository.findAll());
    }

    @Override
    public List<PermissionsMapDto> findAllGroupByModule() {
        Map<String, List<Permission>> listMap = permissionRepository.findAll()
                .stream().collect(Collectors.groupingBy(Permission::getModule));

        List<PermissionsMapDto> mapDtoList = new ArrayList<>();

        for (Map.Entry<String, List<Permission>> data: listMap.entrySet()){
            List<PermissionDto> permissionDtoList = new ArrayList<>();
            for (Permission permissions : data.getValue()){
                PermissionDto permissionDto = new PermissionDto();
                permissionDto.setId(Optional.of(permissions.getId()));
                permissionDto.setName(Optional.of(permissions.getName()));
                permissionDto.setDescription(Optional.of(permissions.getDescription()));
                permissionDtoList.add(permissionDto);
            }
            PermissionsMapDto mapDto = new PermissionsMapDto();
            mapDto.setModule(data.getKey());
            mapDto.setAuthorities(permissionDtoList);
            mapDtoList.add(mapDto);
        }
        return mapDtoList;
    }

    @Override
    public List<PermissionDto> findByRole(PermissionDto permissionDto) {
        return null;
    }

    @Override
    public List<Permission> findByModule(String module) {
        return this.permissionRepository.findAllByModuleContainsIgnoreCase(module);
    }

    @Override
    public PermissionDto findOne(PermissionDto permissionDto) {
        return null;
    }

    @Override
    public PermissionDto findByName(PermissionDto permissionDto) {
        return null;
    }

    @Override
    public PermissionDto findByNameLike(PermissionDto permissionDto) {
        return null;
    }

    @Override
    public PermissionDto updateOne(PermissionDto permissionDto) {
        return null;
    }

    @Override
    public List<PermissionDto> updateInBatch(List<PermissionDto> permissionDtoList) {
        return null;
    }

    private Permission mapDtoToModel(PermissionDto permissionDto) {
        Permission permission = new Permission();
        if (permissionDto.getId().isPresent())
            permission.setId(permissionDto.getId().get());
        if (permissionDto.getDescription().isPresent())
            permission.setDescription(permissionDto.getDescription().get());
        if (permissionDto.getName().isPresent())
            permission.setName(permissionDto.getName().get());
        return permission;
    }

    private PermissionDto mapModelToDto(Permission permission) {
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setId(Optional.of(permission.getId()));
        permissionDto.setName(Optional.of(permission.getName()));
        permissionDto.setDescription(Optional.of(permission.getDescription()));
        return permissionDto;
    }

    public Set<Permission> mapDtoListToModelList(List<PermissionDto> permissionDtoList) {
        Set<Permission> permissions = new HashSet<>();
        if (!permissionDtoList.isEmpty()) {
            for (PermissionDto permissionDto : permissionDtoList) {
                Permission permission = new Permission();
//                if (permissionDto.getName().isPresent())
//                    permission.setName(permissionDto.getName().get());
                if (permissionDto.getId().isPresent()) {
//                    permission.setId(permissionDto.getId().get());
                    permission = permissionRepository.getOne(permissionDto.getId().get());
                }
//                if (permissionDto.getDescription().isPresent())
//                    permission.setDescription(permissionDto.getDescription().get());
                permissions.add(permission);
            }
        }
        return permissions;
    }

    public List<PermissionDto> mapModelListToDtoList(List<Permission> permissions) {
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        if (!permissions.isEmpty()) {
            for (Permission permission : permissions) {
                PermissionDto permissionDto = new PermissionDto();
                permissionDto.setId(Optional.of(permission.getId()));
                permissionDto.setName(Optional.of(permission.getName()));
                permissionDto.setDescription(Optional.of(permission.getDescription()));
                permissionDtoList.add(permissionDto);
            }
        }
        return permissionDtoList;
    }

}
