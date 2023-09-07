package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.lab.dto.parasitology.LabParasitologyTemplateDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabResultPrepDto {
	private PatientDetailDto patientDetailDto;
	private String requestNumber;
	private String specimen;
	private String requestDate;
	private String requestingDoctor;
	private Long testRequestId; //parent test request id
	private List<LabResultTestParamDto> testParameterList;
	private Long singleTestRequestItemId;
	private LabDepartmentTypeEnum resultTypeEnum;
	private LabParasitologyTemplateDto parasitologyTemplate;

	// add the following properties for report lab result printing.
	private String labNote;
	private String labNoteBy;
	private UserDto preparedBy;
	private DepartmentDto preparedFrom;
	private LocalDate reportDate;
	private UserDto VerifiedBy;
	private LocalDate VerifiedDate;
	//
	private String testName;
	private String pathologistComment;
	private String pathologistName;
	private String preparedLabNote;

	private String provisionalDiagnosis;
	private String provisionalDiagnosisBy;

}
