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
public class VitalSignDto {
	private Long id;
	private PatientDetailDto patient;
	private UserDto capturedBy;
	private String capturedByLabel;
	private DepartmentDto captureFromLocation;
	private Double weight;
	private Double height;
	private Double bodyMassIndex;
	private Double temperature;
	private Double bodySurfaceArea;
	private Double respiratoryRate;
	private Double pulseRate;
	private Double systolicBp;
	private Double diastolicBp;
	private Double randomBloodSugar;
	private Double fastBloodSugar;
	private Double oxygenSaturation;
	private Double painScore;
	private Double urineAnalysis;
	private String commentRemark;
	private UserDto assignTo;
	private Boolean isNurse;
	private Boolean isDoctor;
}
