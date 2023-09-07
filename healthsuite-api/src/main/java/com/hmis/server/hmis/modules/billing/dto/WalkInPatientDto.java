package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.GenderDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class WalkInPatientDto {

	private Long id;
	private String firstName;
	private String lastName;
	private String otherName;
	private String address;
	private String phone;
	private String age;
	private Long genderId;
	private GenderDto gender;

	public WalkInPatientDto(String firstName, String lastName, String otherName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.otherName = otherName;
	}

	public WalkInPatientDto(Long id, String firstName, String lastName, String otherName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.otherName = otherName;
	}
}
