package com.hmis.server.hmis.common.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.RoleDto;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class UserDto {
	// Todo::add validation to this bean
	private Optional< Long > id;
	private Optional< String > lastName;
	private Optional< String > otherNames;
	@Email
	private Optional< String > email;
	private Optional< String > phone;
	private Optional< DepartmentDto > department;
	private Optional< List< RoleDto > > role;
	private Optional< String > userName;
	private Optional< String > password;
	private Optional< DateDto > expiryDate;
	private Optional< Boolean > accountEnabled;
	private Optional< String > confirmPassword;
	private Optional< String > firstName;
	private Optional< String > label;

	public UserDto(Optional< Long > id) {
		this.id = id;
	}

	public UserDto(Optional< Long > id, Optional< String > userName) {
		this.id = id;
		this.userName = userName;
	}

	public UserDto(Long id, String label) {
		this.id = Optional.of(id);
		this.label = Optional.ofNullable(label);
	}
}
