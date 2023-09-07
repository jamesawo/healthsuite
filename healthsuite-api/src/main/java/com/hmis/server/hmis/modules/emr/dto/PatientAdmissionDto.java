package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.BedDto;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.WardDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientAdmissionDto {
	private Long id;
	private PatientDetailDto patient;
	private Long patientId;
	private WardDto ward;
	private Long wardId;
	private BedDto bed;
	private Long bedId;
	private DateDto admissionDate;
	private String admittedTime;
	private DateDto dischargedDate;
	private UserDto consultant;
	private Long consultantId;
	private String admissionCode;
	private PatientAdmissionStatusEnum admissionStatus;
	private UserDto admittedBy;
	private DepartmentDto location;

	public PatientAdmissionDto(String admissionCode){
		this.admissionCode = admissionCode;
	}

	public PatientAdmissionDto(Long id, String admissionCode){
		this.id = id;
		this.admissionCode = admissionCode;
	}
}
