package com.hmis.server.hmis.modules.settings.service;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    CommonService commonService;

    @Autowired
    UserManagerService userManagerService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    public ResponseDto findAllUser(){
        /*
            todo:: filter the non required fields from the result of get all user || return username, email and phone number (this will reduce response load/time)
        * */
        return userManagerService.findAllUser();
    }


   public ResponseDto resetUserPassword(UserDto userDto){
        return userManagerService.resetUserPassword(userDto);
   }

}
