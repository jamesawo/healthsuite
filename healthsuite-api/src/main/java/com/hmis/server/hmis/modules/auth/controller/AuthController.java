package com.hmis.server.hmis.modules.auth.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.auth.dto.LoginDto;
import com.hmis.server.hmis.modules.auth.dto.RegisterDto;
import com.hmis.server.hmis.modules.auth.service.AuthServiceImpl;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import com.hmis.server.hmis.modules.settings.service.GlobalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HmisConstant.API_PREFIX +"/auth/")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;
	@Autowired
	private GlobalSettingsService globalSettingsService;

	@PostMapping( "default-user" )
    public ResponseDto register(@RequestBody RegisterDto registerDto){
		/* Seed default user */
		return authService.register(registerDto);
    }

    @PostMapping("login")
    public ResponseDto login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("logout")
    public void logout(@RequestBody LoginDto loginDto){
        authService.updateUserTokenNotExpired(loginDto.getUsername(), false);
    }

	@PostMapping( "default-role" )
    public ResponseDto createDefaultRole(@RequestBody RoleDto roleDto){
		/* Seed default role */
        return authService.createDefaultRole(roleDto);
    }

	@PostMapping( "/add-global-setting-key-value" )
	public ResponseDto addKeyValue(@RequestBody GlobalSettingsDto dto) {
		return this.globalSettingsService.createIfNotExist(dto);
	}
}
