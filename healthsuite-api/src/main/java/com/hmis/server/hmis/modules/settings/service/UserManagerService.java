package com.hmis.server.hmis.modules.settings.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.auth.service.AuthServiceImpl;
import com.hmis.server.hmis.modules.settings.dto.UpdateRoleDto;
import com.hmis.server.hmis.modules.settings.dto.UpdateUserDetailDto;
import com.hmis.server.hmis.modules.settings.dto.UserChangePasswordDto;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagerService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserServiceImpl userService;
    @Autowired
    CommonService commonService;
    @Autowired
    private AuthServiceImpl authService;


    public ResponseDto findAllUser() {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Get All User";
        try {
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
            responseDto.setHttpStatusText(HmisConstant.STATUS_200);
            responseDto.setData(userService.findAll());
            LOGGER.debug(commonService.getLoggerMessage(message, HmisConstant.LOGGER_TYPE_PASSED));
        } catch (Exception e) {
            responseDto.setHttpStatusText(e.toString());
            responseDto.setMessage(e.getMessage());
            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
        }
        return responseDto;
    }

    public ResponseDto createOneUser(UserDto userDto) {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Create a new user ";
        if (userService.isUserExist(userDto)) {
            responseDto.setHttpStatusText(HmisConstant.STATUS_409);
            responseDto.setHttpStatusCode(409);
            responseDto.setMessage(HmisConstant.ENTITY_EXIST);
            return responseDto;
        } else {
            try {
                responseDto.setData(userService.createOne(userDto));
                responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
                responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
                responseDto.setHttpStatusText(HmisConstant.STATUS_200);
                LOGGER.debug(commonService.getLoggerMessage(message, HmisConstant.LOGGER_TYPE_PASSED));
            } catch (Exception e) {
                responseDto.setHttpStatusText(e.toString());
                responseDto.setMessage(e.getMessage());
                commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
            }
            return responseDto;
        }

    }


    public ResponseDto resetUserPassword(UserDto userDto) {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Reset User Password ";
        try {
            userService.resetUserPassword(userDto);
            responseDto.setData(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
            responseDto.setHttpStatusText(HmisConstant.STATUS_200);
            LOGGER.debug(commonService.getLoggerMessage(message, HmisConstant.LOGGER_TYPE_PASSED));
        } catch (Exception e) {
            responseDto.setHttpStatusText(Arrays.toString(e.getStackTrace()));
            responseDto.setMessage(e.getMessage());
            responseDto.setHttpStatusCode(401);
            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
        }
        return responseDto;
    }


    public ResponseDto findAllConsultants() {
        ResponseDto responseDto = new ResponseDto();
        try {
            responseDto.setMessage("Searching");
            List<UserDto> userList = this.userService.findAllByPermissionsGroup(HmisConstant.DOCTOR_PERMISSION_GROUP);
            responseDto.setData(userList);
        } catch (Exception e) {
            commonService.setLOGGER(HmisConstant.DEBUG, e.getMessage(), Optional.of(""));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return responseDto;
    }

    public List<UserDto> searchUser(String searchTerm, boolean isConsultant, boolean showDisabledUser) {

        try {
            return this.userService.simpleSearchUser(searchTerm, isConsultant, showDisabledUser);
        } catch (Exception e) {
            commonService.setLOGGER(HmisConstant.DEBUG, e.getMessage(), Optional.empty());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }

    }

    public UserDto findUserAndRoleData(long userId) {
        User user = this.userService.findOneRaw(userId);
        return this.userService.mapModelToDto(user);
    }

    public ResponseEntity<Boolean> changeUserPassword(UserChangePasswordDto dto) {
        this.validateChangePasswordRequest(dto);
        try {
            User user = this.userService.findOneRaw(dto.getUserId());
            boolean updated = this.userService.changeUserPassword(dto.getNewPassword(), user);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void validateChangePasswordRequest(UserChangePasswordDto dto) {
        if (dto.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is Required");
        }
        if (dto.getOldPassword() == null || dto.getNewPassword() == null || dto.getConfirmPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Password & Confirm Password Don't Match");
        }

        boolean isPasswordMatch = this.authService.isPasswordMatch(dto.getUserId(), dto.getOldPassword());
        if (!isPasswordMatch) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old Password Don't Match.");
        }

    }

    public ResponseEntity<Boolean> updateUserRole(UpdateRoleDto dto) {
        if (dto.getUserId() == null || dto.getRoleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role/User is Required");
        }

        try {
            boolean updated = this.userService.updateUserRole(dto.getUserId(), dto.getRoleId());
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    public ResponseEntity<MessageDto> updateUserDetails(UpdateUserDetailDto dto) {
        if (ObjectUtils.isEmpty(dto.getUserId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");
        try {
            boolean updated = this.userService.updateUserDetails(dto);
                return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
