package com.hmis.server.hmis.modules.nurse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.SpecialityUnitDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class PatientAntenatalDto {
	private Long id;
	private PatientDetailDto patient;
	private UserDto bookedBy;
	private DepartmentDto bookedFrom;
	private UserDto caseConsultant;
	private SpecialityUnitDto specialityUnit;
	private DepartmentDto clinic;
	private String spouseName;
	private String spousePhone;
	private String spouseOccupation;
	private String spouseEmployer;
}
