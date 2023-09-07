package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.settings.dto.UpdateRoleDto;
import com.hmis.server.hmis.modules.settings.dto.UpdateUserDetailDto;
import com.hmis.server.hmis.modules.settings.dto.UserChangePasswordDto;
import com.hmis.server.hmis.modules.settings.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/user-manager/")
public class UserManagerController {

	@Autowired
	UserManagerService userManagerService;

	@GetMapping("user/all")
	public ResponseDto currentUsersList() {
		return userManagerService.findAllUser();
	}

	@PostMapping("user/create")
	public ResponseDto createOneUser(@RequestBody UserDto userDto) {
		return userManagerService.createOneUser(userDto);
	}

	//todo:: remove block not in use
	@GetMapping("user/get-consultants")
	public ResponseDto findAllConsultants() {
		return new ResponseDto();
	}

	@GetMapping(value = "search-user")
	public List<UserDto> searchUsers(
			@RequestParam("search") String search,
			@RequestParam("isConsultant") boolean isConsultant,
			@RequestParam("showDisabledUser") boolean showDisabledUser
	) {
		return userManagerService.searchUser(search, isConsultant, showDisabledUser);
	}

	@GetMapping(value = "user/get-role/{userId}")
	public UserDto getUserRole(@PathVariable String userId){
		return this.userManagerService.findUserAndRoleData(Long.parseLong(userId));
	}

	@PostMapping(value = "change-password")
	public ResponseEntity<Boolean> changeUserPassword(@RequestBody UserChangePasswordDto dto){
		return this.userManagerService.changeUserPassword(dto);
	}

	@PostMapping(value = "update-role")
	public ResponseEntity<Boolean> updateUserRole(@RequestBody UpdateRoleDto dto){
		return this.userManagerService.updateUserRole(dto);
	}

	@PostMapping(value = "update-user-detail")
	public ResponseEntity<MessageDto> updateUserDetails(@RequestBody UpdateUserDetailDto dto) {
		return this.userManagerService.updateUserDetails(dto);
	}


}
