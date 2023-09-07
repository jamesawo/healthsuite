package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Optional;

import com.hmis.server.hmis.common.common.dto.NationalityDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PatientContactDetailDto {
	private Optional< Long > id;
	private String residentialAddress;
	private String email;
	private String phoneNumber;
	private Long nationalityId;
	private NationalityDto nationality;

	public PatientContactDetailDto(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
