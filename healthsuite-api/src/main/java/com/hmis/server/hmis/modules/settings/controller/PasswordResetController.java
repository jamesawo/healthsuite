package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.settings.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/password/")
public class PasswordResetController {

    @Autowired
    PasswordResetService passwordResetService;

    @GetMapping("getAllUser")
    public ResponseDto getAllUsers(){
        return passwordResetService.findAllUser();
    }

    @PostMapping("resetUserPassword")
    public ResponseDto resetUserPassword(@RequestBody UserDto userDto){
        return passwordResetService.resetUserPassword(userDto);
    }

}
