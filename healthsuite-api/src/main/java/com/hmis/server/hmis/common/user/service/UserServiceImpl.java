package com.hmis.server.hmis.common.user.service;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.model.Role;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.common.service.PermissionServiceImpl;
import com.hmis.server.hmis.common.common.service.RoleServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.repository.UserRepository;
import com.hmis.server.hmis.modules.auth.service.AuthServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.hmis.server.hmis.modules.settings.dto.UpdateUserDetailDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final AuthServiceImpl authService;
    private final RoleServiceImpl roleService;
    private final DepartmentServiceImpl departmentService;
    private final PermissionServiceImpl permissionService;
    private final HmisUtilService utilService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            @Lazy AuthServiceImpl authService,
            RoleServiceImpl roleService,
            DepartmentServiceImpl departmentService,
            PermissionServiceImpl permissionService,
            HmisUtilService utilService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.roleService = roleService;
        this.departmentService = departmentService;
        this.permissionService = permissionService;
        this.utilService = utilService;
    }

    public User getUserDetailsByUserName(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseGet(User::new);
    }

    @Override
    public UserDto findOne(UserDto userDto) throws EntityNotFoundException {
        //Todo:: handle exception properly when user id is not present
        if (userDto.getId().isPresent()) {
            Optional<User> optionalUser = userRepository.findById(userDto.getId().get());
            if (optionalUser.isPresent()) {
                return mapModelToDto(optionalUser.get());
            } else {
                throw new EntityNotFoundException(HmisConstant.STATUS_409, "Cannot Find User With ID Provided");
            }
        } else {
            return new UserDto();
        }

    }

    @Override
    public UserDto findByEmail(UserDto userDto) throws EntityNotFoundException {
        if (userDto.getEmail().isPresent()) {
            Optional<User> byEmail = userRepository.findByEmail(userDto.getEmail().get());
            if (byEmail.isPresent()) {
                return mapModelToDto(byEmail.get());
            } else {
                throw new EntityNotFoundException("404", "Cannot Any User With Email Provided");
            }
        } else {
            return new UserDto();
        }

    }

    @Override
    public UserDto createOne(UserDto userDto) {
        if (!isUserExist(userDto)) {

            if (!isPasswordMatchConfirmPassword(userDto)) {
                throw new HmisApplicationException("Password & Confirm Password Doesnt Not Match");
            }
            return mapModelToDto(userRepository.save(mapDtoToModel(userDto)));

        } else {
            throw new HmisApplicationException(HmisConstant.ENTITY_EXIST);
        }
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> userDtoList = new ArrayList<>();
        List<User> users = userRepository.findAll();
        if (users.size() > 0) {
            userDtoList = mapModelListToDtoList(users.stream().filter(user -> !user.getUserName().equalsIgnoreCase(HmisConstant.SUPER_ADMIN_USERNAME)).collect(Collectors.toList()));
        }
        return userDtoList;
    }

    @Override
    public List<UserDto> findByDepartment(UserDto userDto) throws BadRequestException {
        if (userDto.getDepartment().isPresent()) {
            if (userDto.getDepartment().get().getId().isPresent()) {
                return mapModelListToDtoList(userRepository.findByDepartment(departmentService.mapDtoToModel(userDto.getDepartment().get())));
            } else {
                throw new BadRequestException(HmisConstant.STATUS_404, "Provide a department before searching for users");
            }
        } else {
            throw new BadRequestException(HmisConstant.STATUS_400, "Invalid Request, No Department Provided.");
        }

    }

    @Override
    public List<UserDto> createInBatch(List<UserDto> userDtoList) {
        return null;
    }

    @Override
    public Map<String, List<Object>> createInBatchFromExcel(MultipartFile file) throws IOException {
        return null;
    }

    @Override
    public UserDto updateOne(UserDto userDto) throws EntityNotFoundException {
        if (userDto.getId().isPresent()) {
            User user = userRepository.getOne(userDto.getId().get());
            if (user != null) {
                setModelForUpdate(userDto, user);
                return mapModelToDto(userRepository.save(user));
            } else {
                throw new EntityNotFoundException(HmisConstant.STATUS_400, "Cannot Find User To Update.");
            }
        } else {
            throw new HmisApplicationException("Provide a user before update");
        }
    }

    @Override
    public void updateInBatch(List<UserDto> userDtoList) {

    }

    @Override
    public void deactivateOne(UserDto userDto) {
        if (userDto.getId().isPresent()) {
            Optional<User> userOptional = userRepository.findById(userDto.getId().get());
            userOptional.ifPresent(user -> userRepository.updateUserAccountEnabled(user.getUserName(), true));
        } else if (userDto.getUserName().isPresent()) {
            Optional<User> byUserName = userRepository.findByUserName(userDto.getUserName().get());
            byUserName.ifPresent(user -> userRepository.updateUserAccountEnabled(byUserName.get().getUserName(), true));
        }
    }

    @Override
    public boolean isUsernameExist(UserDto userDto) {
        boolean flag = false;
        if (userDto.getUserName().isPresent()) {
            flag = userRepository.findAll().stream().anyMatch(user -> user.getUserName().compareToIgnoreCase(userDto.getUserName().get()) == 0);
        }
        return flag;
    }

    @Override
    public boolean isEmailExist(UserDto userDto) {
        boolean flag = false;
        if (userDto.getEmail().isPresent()) {
            flag = userRepository.findAll().stream().anyMatch(user -> user.getEmail().compareToIgnoreCase(userDto.getEmail().get()) == 0);
        }
        return flag;
    }

    @Override
    public void resetUserPassword(UserDto userDto) {
        if (userDto.getId().isPresent()) {
            if (userDto.getPassword().isPresent()) {
                if (isPasswordMatchConfirmPassword(userDto)) {
                    User user = userRepository.getOne(userDto.getId().get());
                    user.setPassword(authService.encodePassword(userDto.getPassword().get()));
                    userRepository.save(user);

                } else {
                    throw new HmisApplicationException("Password Does Not Match");
                }
            } else {
                throw new HmisApplicationException("No Password Provided");
            }
        } else {
            throw new HmisApplicationException("No User Provided Before Password Reset");
        }
    }

    @Override
    public boolean isPasswordMatchConfirmPassword(UserDto userDto) {
        if (userDto.getPassword().isPresent() && userDto.getConfirmPassword().isPresent()) {
            return userDto.getPassword().get().compareToIgnoreCase(userDto.getConfirmPassword().get()) == 0;
        } else {
            throw new HmisApplicationException("Provide Password And Confirm Password First Before IsMatch Check");
        }
    }

    @Override
    public List<UserDto> simpleSearchUser(String search, boolean isConsultant, boolean showDisabledUser) {
        List<UserDto> userDtoList = new ArrayList<>();
        List<User> userList = this.userRepository.findAllByUserNameContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(search, search, search);
        if (userList.size() > 0) {
            for (User user : userList) {
                if (showDisabledUser) {
                    UserDto userDto = this.addSearchUser(user, isConsultant);
                    if (userDto != null)
                        userDtoList.add(userDto);
                } else {
                    boolean userEnabled = isUserEnabled(user);
                    if (userEnabled) {
                        UserDto userDto = this.addSearchUser(user, isConsultant);
                        if (userDto != null)
                            userDtoList.add(userDto);
                    }
                }

            }
        }
        return userDtoList;
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> optional = this.userRepository.findByUserName(username);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
        }
        return optional.get();
    }

    public boolean isUserExist(UserDto userDto) {
        return isUsernameExist(userDto) || isEmailExist(userDto);
    }

    public List<UserDto> findAllByPermissionsGroup(String permissionGroup) {
        List<Permission> permissions = this.permissionService.findByModule(permissionGroup);
        return this.filterConsultant(permissions);
    }

    public User findOneRaw(Long consultantId) {
        Optional<User> user = this.userRepository.findById(consultantId);
        if (!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found, Id: " + consultantId);
        }
        return user.get();
    }

    public User findOneFromDto(UserDto dto) {
        if (dto == null || !dto.getId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Id not present");
        }
        return this.findOneRaw(dto.getId().get());
    }

    public UserDto setConsultantBasicDetails(User user) {
        UserDto userDto = new UserDto();
        if (user.getEmail() != null) {
            userDto.setEmail(Optional.of(user.getEmail()));
        }
        if (user.getFirstName() != null) {
            userDto.setFirstName(Optional.of(user.getFirstName()));
        }
        if (user.getLastName() != null) {
            userDto.setLastName(Optional.of(user.getLastName()));
        }

        return userDto;
    }

    // use mapDtoToClean instead (removes sensitive user data like roles and permission )
    public UserDto mapModelToDto(User user) {
        //todo:: remove role & other user before transforming to dto
        UserDto userDto = new UserDto();
        Optional<String> label = Optional.of("");

        if (user.getId() != null) {
            userDto.setId(Optional.of(user.getId()));
        }

        if (user.getEmail() != null) {
            userDto.setEmail(Optional.of(user.getEmail()));
        }

        if (user.getPhoneNumber() != null) {
            userDto.setPhone(Optional.of(user.getPhoneNumber()));
        }

        if (user.getLastName() != null) {
            userDto.setLastName(Optional.of(user.getLastName()));
            label = Optional.of(user.getLastName());
        }

        if (user.getFirstName() != null) {
            userDto.setFirstName(Optional.of(user.getFirstName()));
            userDto.setOtherNames(Optional.of(user.getFirstName()));
            label = Optional.of(label.get() + " " + user.getFirstName());
            userDto.setLabel(label);
        }

        if (user.getExpiryDate() != null) {
            userDto.setExpiryDate(Optional.of(this.utilService.transformToDateDto(user.getExpiryDate())));
        }

        if (user.getDepartment() != null) {
            userDto.setDepartment(Optional.of(departmentService.mapModelToDto(user.getDepartment())));
        }

        if (user.getRoles() != null) {
            userDto.setRole(Optional.of(roleService.mapModelListToDtoList((List<Role>) user.getRoles())));
        }

        if (user.getEnabled() != null) {
            userDto.setAccountEnabled(Optional.of(user.getEnabled()));
        }

        if (user.getUserName() != null) {
            userDto.setUserName(Optional.of(user.getUserName()));
        }

        return userDto;
    }

    public UserDto mapToDtoClean(User user) {
        user.setDepartment(null);
        user.setRoles(null);
        user.setExpiryDate(null);
        user.setAccountType(null);
        user.setPassword(null);
        user.setEnabled(null);
        return this.mapModelToDto(user);
    }

    public boolean updateUserRole(Long userId, Long roleId) {
        try {
            User user = this.findOneRaw(userId);
            Role role = this.roleService.findOneRaw(roleId);
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            this.userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean changeUserPassword(String password, User user) {
        String encodePassword = this.authService.encodePassword(password);
        int i = this.userRepository.updateUserPassword(encodePassword, user.getId());
        return i != 0;
    }

    public boolean updateUserDetails(UpdateUserDetailDto dto){
        User user = this.findOneRaw(dto.getUserId());
        if (ObjectUtils.isNotEmpty(dto.getDepartment() ) && ObjectUtils.isNotEmpty(dto.getDepartment().getId())){
            user.setDepartment(this.departmentService.findOne(dto.getDepartment().getId().get()));
        }
        /*
        if ( dto.getRole() != null && dto.getRole().getId().isPresent()) {
            Role role = this.roleService.findOneRaw(dto.getRole().getId().get());
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
        }
         */
        user.setFirstName(dto.getLastName());
        user.setLastName(dto.getOtherNames());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setExpiryDate(this.utilService.transformToLocalDate(dto.getAccExpiryDate()));
        this.userRepository.save(user);
        return true;
    }

    private boolean isUserEnabled(User user) {
        if (user.getExpiryDate() == null) {
            user.setExpiryDate(LocalDate.now().plusMonths(1));
            this.userRepository.save(user);
        }
        return user.getEnabled() && user.getExpiryDate().isAfter(LocalDate.now());
    }

    private UserDto addSearchUser(User user, boolean isConsultant) {
        UserDto userDto = null;
        if (isConsultant) {
            if (this.isConsultant(user)) {
                userDto = mapToDtoClean(user);
            }
        } else {
            userDto = mapToDtoClean(user);
        }
        return userDto;
    }

    private boolean isConsultant(User user) {
        Collection<Role> roles = user.getRoles();
        if (roles != null) {
            return this.roleService.hasAnyConsultantPermission(roles);
        }
        return false;
    }

    private List<UserDto> filterConsultant(List<Permission> appPermissions) {
        List<UserDto> returnUserList = new ArrayList<>();
        HashSet<Role> roles = this.roleService.findAllByPermissionIn(appPermissions);
        List<Role> filteredRoles = new ArrayList<>();
        for (Role role : roles) {
            String roleName = role.getName().replaceAll(" ", "").toLowerCase();
            if (roleName.contains(HmisConstant.SUPER_ADMIN_ROLE.toLowerCase()) ||
                    roleName.contains(HmisConstant.SUPPORT_ROLE.toLowerCase()) ||
                    roleName.startsWith(HmisConstant.PMO_ROLE_PREFIX.toLowerCase())) {
                role.getName();
            } else {
                filteredRoles.add(role);
            }
        }
        HashSet<User> userHashSet = this.userRepository.findAllByRolesIn(filteredRoles);
        if (userHashSet.size() > 0) {
            returnUserList = userHashSet.stream().map(this::mapModelToDto).collect(Collectors.toList());
        }
        return returnUserList;
    }

    private User mapDtoToModel(UserDto userDto) {
        User user = new User();
        setModel(userDto, user);
        return user;
    }

    private void setModel(UserDto userDto, User user) {
        if (userDto.getId().isPresent()) {
            user.setId(userDto.getId().get());
        }
        if (userDto.getUserName().isPresent()) {
            user.setUserName(userDto.getUserName().get());
        }
        if (userDto.getLastName().isPresent()) {
            user.setLastName(userDto.getLastName().get());
        }
        if (userDto.getOtherNames().isPresent()) {
            user.setFirstName(userDto.getOtherNames().get());
        }
        if (userDto.getPhone().isPresent()) {
            user.setPhoneNumber(userDto.getPhone().get());
        }
        if (userDto.getEmail().isPresent()) {
            user.setEmail(userDto.getEmail().get());
        }
        if (userDto.getPassword().isPresent()) {
            user.setPassword(authService.encodePassword(userDto.getPassword().get()));
        }

        if (userDto.getExpiryDate().isPresent()) {
            LocalDate localDate = LocalDate.of(userDto.getExpiryDate().get().getYear(), userDto.getExpiryDate().get().getMonth(), userDto.getExpiryDate().get().getDay());
            user.setExpiryDate(localDate);
        }

        if (userDto.getDepartment().isPresent()) {
            user.setDepartment(departmentService.findOneRaw(userDto.getDepartment().get().getId()));
        }

        if (userDto.getRole().isPresent()) {
            List<Role> roles = new ArrayList<>();
            userDto.getRole().get().forEach(x -> {
                roles.add(new Role(x.getId().get()));
            });
            user.setRoles(roles);
        }

    }

    private List<User> mapDtoListToModelList(List<UserDto> userDtoList) {
        List<User> userList = new ArrayList<>();
        if (!userDtoList.isEmpty()) {
            for (UserDto userDto : userDtoList) {
                User user = new User();
                if (userDto.getId().isPresent()) {
                    user.setId(userDto.getId().get());
                }
                if (userDto.getEmail().isPresent()) {
                    user.setEmail(userDto.getEmail().get());
                }
                if (userDto.getLastName().isPresent()) {
                    user.setLastName(userDto.getLastName().get());
                }
                if (userDto.getOtherNames().isPresent()) {
                    user.setFirstName(userDto.getOtherNames().get());
                }
                if (userDto.getPhone().isPresent()) {
                    user.setPhoneNumber(userDto.getPhone().get());
                }
                if (userDto.getExpiryDate().isPresent()) {
                    user.setExpiryDate(this.utilService.transformToLocalDate(userDto.getExpiryDate()
                            .get()));
                }
                if (userDto.getDepartment().isPresent()) {
                    user.setDepartment(departmentService.mapDtoToModel(userDto.getDepartment().get()));
                }
                if (userDto.getRole().isPresent()) {
                    user.setRoles(roleService.mapDtoListToModelList(userDto.getRole().get()));
                }
                if (userDto.getPassword().isPresent()) {
                    user.setPassword(authService.encodePassword(userDto.getPassword().get()));
                }
                userList.add(user);
            }
        }
        return userList;

    }

    private List<UserDto> mapModelListToDtoList(List<User> userList) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            UserDto userDto = new UserDto();
            if (user.getId() != null) {
                userDto.setId(Optional.of(user.getId()));
            }
            if (user.getLastName() != null) {
                userDto.setLastName(Optional.of(user.getLastName()));
            }
            if (user.getFirstName() != null) {
                userDto.setOtherNames(Optional.of(user.getFirstName()));
            }
            if (user.getEmail() != null) {
                userDto.setEmail(Optional.of(user.getEmail()));
            }
            if (user.getPhoneNumber() != null) {
                userDto.setPhone(Optional.of(user.getPhoneNumber()));
            }
            if (user.getDepartment() != null) {
                userDto.setDepartment(Optional.of(departmentService.mapModelToDto(user.getDepartment())));
            }
            if (!user.getRoles().isEmpty()) {
                userDto.setRole(Optional.of(roleService.mapModelListToDtoList((List<Role>) user.getRoles())));
            }
            if (user.getUserName() != null) {
                userDto.setUserName(Optional.of(user.getUserName()));
            }
            if (user.getExpiryDate() != null) {
                userDto.setExpiryDate(Optional.of(this.utilService.transformToDateDto(user.getExpiryDate())));
            }
            if (user.getEnabled() != null) {
                userDto.setAccountEnabled(Optional.of(user.getEnabled()));
            }
            userDtoList.add(userDto);
        }
        return userDtoList;

    }

    private void setModelForUpdate(UserDto userDto, User user) {
        if (userDto.getLastName().isPresent() && !userDto.getLastName().get().isEmpty()) {
            user.setLastName(userDto.getLastName().get());
        }
        if (userDto.getOtherNames().isPresent() && !userDto.getOtherNames().get().isEmpty()) {
            user.setFirstName(userDto.getOtherNames().get());
        }
        if (userDto.getEmail().isPresent() && !userDto.getEmail().get().isEmpty()) {
            user.setEmail(userDto.getEmail().get());
        }
        if (userDto.getPhone().isPresent() && !userDto.getPhone().get().isEmpty()) {
            user.setPhoneNumber(userDto.getPhone().get());
        }
        if (userDto.getDepartment().isPresent()) {
            if (userDto.getDepartment().get().getId().isPresent()) {
                user.setDepartment(departmentService.mapDtoToModel(userDto.getDepartment().get()));
            }
        }
        if (userDto.getRole().isPresent()) {
            if (!userDto.getRole().get().isEmpty()) {
                user.setFirstName(userDto.getOtherNames().get());
                user.setRoles(roleService.mapDtoListToModelList(userDto.getRole().get()));
            }
        }
        if (userDto.getUserName().isPresent() && !userDto.getUserName().get().isEmpty()) {
            user.setUserName(userDto.getUserName().get());
        }
        if (userDto.getExpiryDate().isPresent() && userDto.getExpiryDate().toString() != null) {
            user.setExpiryDate(this.utilService.transformToLocalDate(userDto.getExpiryDate()
                    .get()));
        }
        if (userDto.getAccountEnabled().isPresent() && userDto.getAccountEnabled().get()) {
            user.setEnabled(userDto.getAccountEnabled().get());
        }

    }


}
