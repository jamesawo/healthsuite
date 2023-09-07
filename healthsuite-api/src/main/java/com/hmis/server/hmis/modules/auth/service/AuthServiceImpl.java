package com.hmis.server.hmis.modules.auth.service;

import com.hmis.server.hmis.common.common.dto.ModulesDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.model.Role;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.RoleServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.repository.UserRepository;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.auth.dto.LoginDto;
import com.hmis.server.hmis.modules.auth.dto.RegisterDto;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import com.hmis.server.init.security.JwtProvider;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.ENFORCE_SYSTEM_LOCATION;


@Service
public class AuthServiceImpl implements IAuthService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final RoleServiceImpl roleService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final UserServiceImpl userService;
	private final CommonService commonService;
	private final CashierShiftServiceImpl shiftService;
	// private final GlobalSettingsImpl globalSettingsService;

	@Autowired
	public AuthServiceImpl(
			@Lazy RoleServiceImpl roleService,
			@Lazy UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtProvider jwtProvider,
			@Lazy UserServiceImpl userService,
			@Lazy CommonService commonService,
			@Lazy CashierShiftServiceImpl shiftService
			//@Lazy GlobalSettingsImpl globalSettingsService
			) {
		this.roleService = roleService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtProvider = jwtProvider;
		this.userService = userService;
		this.commonService = commonService;
		this.shiftService = shiftService;
		// this.globalSettingsService = globalSettingsService;
	}

	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public Optional< org.springframework.security.core.userdetails.User > getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = ( org.springframework.security.core.userdetails.User ) SecurityContextHolder.
				getContext().getAuthentication().getPrincipal();
		return Optional.of(principal);
	}

	@Override
	public ResponseDto login(LoginDto loginDto) {
		ResponseDto responseDto = new ResponseDto();
		this.validateUser(loginDto);
		try {
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authenticate);
			responseDto.setDate(new Date());
			responseDto.setData(this.prepareUserMapParameter(loginDto));
			responseDto.setMessage("Bearer " + jwtProvider.generateToken(authenticate));
			responseDto.setHttpStatusText(HmisConstant.STATUS_200);
			responseDto.setHttpStatusCode(new ResponseEntity(HttpStatus.OK).getStatusCodeValue());
			return responseDto;
		}
		catch( Exception e ) {
			responseDto.setMessage(e.getMessage());
			responseDto.setHttpStatusText(e.toString());
			responseDto.setHttpStatusCode(new ResponseEntity(HttpStatus.BAD_REQUEST).getStatusCodeValue());
			LOGGER.debug(commonService.getAuthenticatedUser() + " login attempt failed: ");
			LOGGER.debug(e.getMessage());
			return responseDto;
		}

	}

	public Map<String, Object> prepareUserMapParameter(LoginDto loginDto){
		User userDetails = userService.getUserDetailsByUserName(loginDto.getUsername());
		List< ModulesDto > userModule = findPermissionsGroupByRole(userDetails.getRoles());
		Map< String, Object > userMap = new HashMap<>();
		userMap.put("username", loginDto.getUsername());
		userMap.put("firstName", userDetails.getUserName());
		userMap.put("lastName", userDetails.getLastName());
		userMap.put("email", userDetails.getEmail());
		userMap.put("userId", userDetails.getId());
		// userMap.put(ENFORCE_SYSTEM_LOCATION, this.globalSettingsService.findValueByKey(ENFORCE_SYSTEM_LOCATION));
		if( userDetails.getDepartment() != null ) {
			userMap.put("department", userDetails.getDepartment().getName());
		}
		Optional<CashierShift> shift = this.shiftService.findActiveShift(userDetails);
		if (shift != null && shift.isPresent() ){
			userMap.put("shiftNumber", shift.get().getCode());
		}
		userMap.put("phone", userDetails.getPhoneNumber());
		userMap.put("modules", userModule);
		return userMap;
	}

	@Override
	public ResponseDto register(@RequestBody RegisterDto registerDto) {
		ResponseDto responseDto = new ResponseDto();
		if( registerDto.getUserName().compareToIgnoreCase(HmisConstant.SUPER_ADMIN_USERNAME) != 0 ) {
			throw new HmisApplicationException(HmisExceptionMessage.REGISTER_NOT_ALLOWED);
		}
		String authUserName = "";
		try {
			User user = userRepository.save(mapDtoToUser(registerDto));
			Map< String, Object > result = new HashMap<>();
			result.put("username", user.getUserName());
			result.put("first_name", user.getFirstName());
			result.put("last_name", user.getLastName());
			result.put("email", user.getEmail());
			result.put("active", user.getEnabled() ? "Yes" : "No");
			result.put("id", user.getId());
			responseDto.setData(result);
			responseDto.setDate(new Date());
			responseDto.setMessage("Registration " + HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(new ResponseEntity(HttpStatus.OK).getStatusCodeValue());
			responseDto.setHttpStatusText(HmisConstant.STATUS_200);
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug("------------------------------------------------------------------");
			LOGGER.debug(authUserName + " attempt to create a new user failed: ");
			LOGGER.debug(e.getMessage());
			LOGGER.debug("------------------------------------------------------------------");
			return responseDto;
		}

	}

	@Override
	public boolean isUserAccountExpired(String username) {
		Optional< User > user = userRepository.findByUserName(username);
		if( user.isPresent() ) {
			if( ! user.get().getUserName().equals(HmisConstant.SUPER_ADMIN_USERNAME) ) {
				//check expiry date validity
				return user.get().getExpiryDate().isBefore(LocalDate.now());
			}
		}
		return false;
	}

	@Override
	public void updateUserAccountNonLocked(String username, boolean isAccountNonLocked) {
		if( userRepository.findByUserName(username).isPresent() ) {
			userRepository.updateUserAccountNonLocked(username, isAccountNonLocked);
		}
	}

	@Override
	public void updateUserAccountEnabled(String username, boolean isAccountEnabled) {
		if( userRepository.findByUserName(username).isPresent() ) {
			userRepository.updateUserAccountEnabled(username, isAccountEnabled);
		}
	}

	@Override
	public void updateUserTokenNotExpired(String username, boolean isTokenNotExpired) {
		if( userRepository.findByUserName(username).isPresent() ) {
			userRepository.updateUserTokenNotExpired(username, isTokenNotExpired);
		}
	}

	@Override
	public void updateUserAccountNotExpired(String username, boolean isAccountNotExpired) {
		if( userRepository.findByUserName(username).isPresent() ) {
			userRepository.updateUserAccountNotExpired(username, isAccountNotExpired);
		}
	}

	@Override
	public List< ModulesDto > findPermissionsGroupByRole(Collection< Role > roles) {
		Map< String, List< Permission > > permissionGroupByModule;
		List< Permission > permissions = new ArrayList<>();
		for( Role role : roles ) {
			for( Permission permission : role.getPermissions() ) {
				if( ! permissions.contains(permission) ) {
					permissions.add(permission);
				}
			}
		}
		permissionGroupByModule = permissions.stream().collect(Collectors.groupingBy(Permission::getModule));
		List< ModulesDto > modulesDtoList = new ArrayList<>();
		for( Map.Entry< String, List< Permission > > list : permissionGroupByModule.entrySet() ) {
			ModulesDto modulesDto = new ModulesDto();
			modulesDto.setName(list.getKey());
			modulesDto.setPermissions(list.getValue());
			modulesDtoList.add(modulesDto);
		}
		return modulesDtoList;
	}

	@Override
	public void validateUser(LoginDto loginDto) {
		if (loginDto.getUsername() != null) {
			Optional<User> user = userRepository.findByUserName(loginDto.getUsername());
			if (!user.isPresent()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Username/Password");
			}

			if (user.get().getExpiryDate().isBefore(LocalDate.now())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Account Has Expired");
			}

			if (!user.get().getEnabled()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Account Is Currently Disabled");
			}

			if (!this.passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Login Credentials");
			}
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
		}
	}

	public ResponseDto createDefaultRole(RoleDto roleDto) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setMessage("Seeded");
		roleService.seedDefaultRole(roleDto);
		return responseDto;
	}

	public boolean isPasswordMatch(Long userId, String password) {
		User user = this.userRepository.getOne(userId);
		return this.passwordEncoder.matches(password, user.getPassword());
	}

	private User mapDtoToUser(RegisterDto registerDto) {
		User user = new User();
		user.setEmail(registerDto.getEmail());
		user.setFirstName(registerDto.getFirstName());
		user.setLastName(registerDto.getLastName());
		user.setPhoneNumber(registerDto.getPhoneNumber());
		user.setPassword(encodePassword(registerDto.getPassword()));
		user.setExpiryDate(registerDto.getAccountExpiry());
		user.setUserName(registerDto.getUserName());
		user.setRoles(registerDto.getRoles());
		if( registerDto.getDepartment() != null ) {
			user.setDepartment(new Department(registerDto.getDepartment().getId().get()));
		}
		if( registerDto.getAccountType() != null ) {
			user.setAccountType(registerDto.getAccountType().toLowerCase());
		}
		return user;
	}


}
