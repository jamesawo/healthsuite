package com.hmis.server.hmis.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.model.Role;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDto {
    public String email;

    public String userName;

    public String password;

    public String phoneNumber;

    public LocalDate accountExpiry;

    public String firstName;

    public String lastName;

    public boolean isEnabled;

    public boolean tokenExpired;

    public List<Role> roles;

    public String accountType;

	public DepartmentDto department;


}
