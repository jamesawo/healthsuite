package com.hmis.server.hmis.common.user.service;

import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IUserService {

    User getUserDetailsByUserName(String username);

    UserDto findOne(UserDto userDto) throws EntityNotFoundException;

    UserDto findByEmail(UserDto userDto) throws EntityNotFoundException;

    UserDto createOne(UserDto userDto);

    List<UserDto> findAll();

    List<UserDto> findByDepartment(UserDto userDto) throws BadRequestException;

    List<UserDto> createInBatch(List<UserDto> userDtoList);

    Map<String, List<Object>> createInBatchFromExcel(MultipartFile file) throws IOException;

    UserDto updateOne(UserDto userDto) throws EntityNotFoundException;

    void updateInBatch(List<UserDto> userDtoList);

    void deactivateOne(UserDto userDto);

    boolean isUsernameExist(UserDto userDto);

    boolean isEmailExist(UserDto userDto);

    void resetUserPassword(UserDto userDto);

    boolean isPasswordMatchConfirmPassword(UserDto userDto);

    List<UserDto> simpleSearchUser(String search, boolean isConsultant, boolean showDisabledUser);


    User findByUsername(String username);
}
