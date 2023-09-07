package com.hmis.server.hmis.modules.auth.service;

import com.hmis.server.hmis.common.common.dto.ModulesDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.model.Role;
import com.hmis.server.hmis.modules.auth.dto.LoginDto;
import com.hmis.server.hmis.modules.auth.dto.RegisterDto;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IAuthService {

    Optional<User> getCurrentUser();

    ResponseDto login(LoginDto loginDto);

    ResponseDto register(RegisterDto registerDto);

    boolean isUserAccountExpired(String username);

    void updateUserAccountNonLocked(String username, boolean isAccountNonLocked);

    void updateUserAccountEnabled(String username, boolean isAccountEnabled);

    void updateUserTokenNotExpired(String username, boolean isTokenNotExpired);

    void updateUserAccountNotExpired(String username, boolean isAccountNotExpired );

    List<ModulesDto> findPermissionsGroupByRole(Collection<Role> roles);

    void validateUser(LoginDto loginDto);


}
