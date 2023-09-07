package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.settings.service.RoleManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/roleManager/")
public class RoleManagerController {
    @Autowired
    RoleManagerService roleManagerService;

    @GetMapping("roleModules/all")
    public ResponseDto getRolesAndPermissions() {
        return roleManagerService.getRolesAndPermissionsWithoutGroup();
    }

    @PostMapping("roleModules/update")
    public ResponseDto updateRolePermissions(@RequestBody RoleDto roleDto) {
        return roleManagerService.updateRolePermissions(roleDto);
    }

    @GetMapping(value = "role-permissions/{roleId}")
    public ResponseEntity findRolePermissions(@PathVariable String roleId){
        return this.roleManagerService.getRolePermissions(Long.parseLong(roleId));
    }
}
