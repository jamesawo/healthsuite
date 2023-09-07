package com.hmis.server.hmis.modules.settings.service;

import com.hmis.server.hmis.common.common.dto.PermissionDto;
import com.hmis.server.hmis.common.common.dto.PermissionsMapDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.PermissionServiceImpl;
import com.hmis.server.hmis.common.common.service.RoleServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RoleManagerService<T> {

    @Autowired
    RoleServiceImpl roleService;

    @Autowired
    PermissionServiceImpl permissionService;

    @Autowired
    CommonService commonService;


    public ResponseDto getRolesAndPermissionsWithGroup() {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Get Roles & Permissions ";
        try {
            responseDto.setData(this.findRolesAndPermission());
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setHttpStatusText(HmisConstant.STATUS_200);
            responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
            responseDto.setDate(new Date());
            return responseDto;
        } catch (Exception e) {
            responseDto.setMessage(e.getMessage());
            responseDto.setHttpStatusText(e.toString());
            responseDto.setHttpStatusCode(500);
            responseDto.setDate(new Date());
            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
            return responseDto;
        }
    }

    public ResponseDto getRolesAndPermissionsWithoutGroup() {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Get Roles & Permissions Without Grouping ";
        try {
            responseDto.setData(this.findRolesAndPermissionWithoutGroup());
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setHttpStatusText(HmisConstant.STATUS_200);
            responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
            responseDto.setDate(new Date());
            return responseDto;
        } catch (Exception e) {
            responseDto.setMessage(e.getMessage());
            responseDto.setHttpStatusText(e.toString());
            responseDto.setHttpStatusCode(500);
            responseDto.setDate(new Date());
            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
            return responseDto;
        }
    }

    public ResponseDto updateRolePermissions(RoleDto roleDto) {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Update Role ";
        try {
            responseDto.setData(roleService.updateOneWithPermissions(roleDto));
            responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setDate(new Date());
            return responseDto;
        } catch (Exception e) {
            responseDto.setMessage(e.getMessage());
            responseDto.setHttpStatusText(e.toString());
            responseDto.setHttpStatusCode(500);
            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
            return responseDto;
        }
    }

    //todo: refactor - code duplicates
    private Map<String, Object> findRolesAndPermission() {
        Map<String, Object> rolesAndPermission = new HashMap<>();
        List<RoleDto> roleDtoList = roleService.findAll();
        List<PermissionsMapDto> permissionDtoList = permissionService.findAllGroupByModule();
        rolesAndPermission.put("roles", roleDtoList);
        rolesAndPermission.put("permissions", permissionDtoList);
        return rolesAndPermission;
    }

    private Map<String, Object> findRolesAndPermissionWithoutGroup() {
        Map<String, Object> rolesAndPermissionWithoutGroup = new HashMap<>();
        List<RoleDto> roleDtoList = roleService.findAll();
        List<PermissionDto> permissionDtoList = permissionService.findAll();
        rolesAndPermissionWithoutGroup.put("roles", roleDtoList);
        rolesAndPermissionWithoutGroup.put("permissions", permissionDtoList);

        return rolesAndPermissionWithoutGroup;
    }

    public ResponseEntity<RoleDto> getRolePermissions(long roleId) {
        try {
            RoleDto role = this.roleService.findPermissionsByRole(roleId);
            return ResponseEntity.status(HttpStatus.OK).body(role);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
