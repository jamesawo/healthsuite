package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IRoleService;
import com.hmis.server.hmis.common.common.dto.PermissionDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.model.Role;
import com.hmis.server.hmis.common.common.repository.RoleRepository;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionServiceImpl permissionService;

    @Override
    public RoleDto createOne(RoleDto roleDto) {
        if (roleDto.getId().isPresent()) {
            if (roleDto.getId().get() == 1L)
                throw new HmisApplicationException("Cannot Modify System Role");
        }
        return mapModelToDto(roleRepository.save(mapDtoToModel(roleDto)));
    }

    @Override
    public List<RoleDto> createInBatch(List<RoleDto> roleDtoList) {
        return null;
    }

    @Override
    public List<RoleDto> findAll() {
        // Remove Super Admin Role From List
        // List<Role> rolesList = this.roleRepository.findAllWithoutSuperAdmin(HmisConstant.SUPER_ADMIN_ROLE);
        List<Role> rolesList = this.roleRepository.findAll();
        return mapModelListToDtoList(rolesList);
    }

    @Override
    public List<PermissionDto> findRolePermissions(RoleDto roleDto) {
        return null;
    }

    @Override
    public List<RoleDto> findByNameLike(RoleDto roleDto) {
        return null;
    }

    @Override
    public RoleDto findByName(RoleDto roleDto) {
        return null;
    }

    @Override
    public RoleDto findOne(RoleDto roleDto) {
        return null;
    }

    @Override
    public RoleDto updateOneWithPermissions(RoleDto roleDto) {
        if (roleDto.getId().isPresent() && !roleDto.getPermissions().isEmpty()) {
            Role role = roleRepository.getOne(roleDto.getId().get());
            role.setPermissions(permissionService.mapDtoListToModelList(roleDto.getPermissions()));
            return mapModelToDto(roleRepository.save(role));
        } else throw new HmisApplicationException("Role or Permissions is not present");
    }

    @Override
    public RoleDto updateOneWithoutPermissions(RoleDto roleDto) {
        if (roleDto.getId().isPresent()) {
            Role role = roleRepository.getOne(roleDto.getId().get());
            if (roleDto.getName().isPresent()) {
                role.setName(roleDto.getName().get());
                return mapModelToDto(roleRepository.save(role));
            } else throw new HmisApplicationException("Cannot Update Role Without Name/Description ");
        } else throw new HmisApplicationException("Cannot Find Role to Update.");
    }

    @Override
    public void deactivateRole(RoleDto roleDto) {
    }

    @Override
    public void activateRole(RoleDto roleDto) {
    }

    @Override
    public void deactivateInBatch(List<RoleDto> roleDtoList) {
    }

    @Override
    public void activateInBatch(List<RoleDto> roleDtoList) {
    }

    @Override
    public boolean isRoleExist(RoleDto roleDto) {
        boolean flag = false;
        if (roleDto.getName().isPresent()) {
            flag = roleRepository.findAll().stream()
                    .anyMatch(role -> role.getName().compareToIgnoreCase(roleDto.getName().get()) == 0);
        }
        return flag;
    }

    @Override
    public Role findOneRaw(long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public List<Role> findAllRaw() {
        return this.roleRepository.findAll();
    }


    @Override
    public HashSet<Role> findAllByPermissionIn(List<Permission> permissions) {
        return this.roleRepository.findAllByPermissionsIn(permissions);
    }

    @Override
    public boolean rolesHasAnyPermission(List<Role> roles, List<Permission> permissions) {
        return false;
    }

    @Override
    public boolean hasAnyConsultantPermission(Collection<Role> roles) {
        List<Permission> consultantPermissions = this.permissionService.findByModule(HmisConstant.DOCTOR_PERMISSION_GROUP);
        List<Permission> permissionsFromRoles = this.getAllPermissionFromRoles(roles);
        return CollectionUtils.containsAny(permissionsFromRoles, consultantPermissions);
    }

    private List<Permission> getAllPermissionFromRoles(Collection<Role> roles) {
        HashSet<Permission> permissions = new HashSet<>();
        if (roles.size() > 0) {
            roles.stream().map(Role::getPermissions).forEach(permissions::addAll);
        }
        return new ArrayList<>(permissions);
    }


    private Role mapDtoToModel(RoleDto roleDto) {
        Role role = new Role();
        setModel(roleDto, role);
        return role;
    }

    private void setModel(RoleDto roleDto, Role role) {

        if (roleDto.getName().isPresent())
            role.setName(roleDto.getName().get());
        if (roleDto.getId().isPresent())
            role.setId(roleDto.getId().get());
        if (roleDto.getDescription() != null && roleDto.getDescription().isPresent())
            role.setDescription(roleDto.getDescription().get());
        if (roleDto.getPermissions() != null && !roleDto.getPermissions().isEmpty())
            role.setPermissions(permissionService.mapDtoListToModelList(roleDto.getPermissions()));
    }

    public RoleDto mapModelToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        if (role.getName() != null)
            roleDto.setName(Optional.ofNullable(role.getName()));
        if (role.getId() != null)
            roleDto.setId(Optional.ofNullable(role.getId()));
        if (role.getPermissions() != null) {
            roleDto.setPermissions(permissionService.mapModelListToDtoList(new ArrayList<>(role.getPermissions())));
        }
        return roleDto;
    }

    public List<Role> mapDtoListToModelList(List<RoleDto> roleDtoList) {
        List<Role> roleList = new ArrayList<>();
        if (!roleDtoList.isEmpty()) {
            for (RoleDto roleDto : roleDtoList) {
                Role role = new Role();
                setModel(roleDto, role);
                roleList.add(role);
            }
        }
        return roleList;
    }

    public List<RoleDto> mapModelListToDtoList(List<Role> roleList) {
        List<RoleDto> roleDtoList = new ArrayList<>();
        if (!roleList.isEmpty()) {
            for (Role role : roleList) {
                RoleDto roleDto = new RoleDto();
                if (role.getId() != null)
                    role = roleRepository.getOne(role.getId());
                roleDto.setId(Optional.ofNullable(role.getId()));
                if (role.getName() != null)
                    roleDto.setName(Optional.ofNullable(role.getName()));
                if (role.getDescription() != null)
                    roleDto.setDescription(Optional.of(role.getDescription()));
                if (role.getPermissions() != null)
                    roleDto.setPermissions(permissionService
                        .mapModelListToDtoList(new ArrayList<>(role.getPermissions())));
                roleDtoList.add(roleDto);
            }
        }
        return roleDtoList;
    }

    private boolean isUserAdminRole(Role role) {
        return role.getName().equals(HmisConstant.SUPER_ADMIN_ROLE) || role.getId() == 1L;
    }

    public void seedDefaultRole(RoleDto roleDto) {
        Role role = new Role();
        if (roleDto.getName().isPresent())
            role.setName(roleDto.getName().get());
        if (roleDto.getDescription().isPresent())
            role.setDescription(roleDto.getDescription().get());
        role.setPermissions( new HashSet<>( permissionService.permissionRepository.findAll() ));
        roleRepository.save(role);
    }

    public RoleDto findPermissionsByRole(long roleId) {
       return this.mapModelToDto(this.findOneRaw(roleId));
    }
}
