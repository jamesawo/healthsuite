package com.hmis.server.hmis.modules.nurse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
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
public class NurseWaitingHistoryDto {
	private Long id;
	private WaitingStatusEnum status;
	private PatientDetailDto patient;
	private DepartmentDto clinic;
	private UserDto attendedBy;
}
