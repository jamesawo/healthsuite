package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.SchemeDto;
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
public class PatientInsuranceDetailDto {
	private Long id;
	private SchemeDto scheme;
	private String primaryProvider;
	private String typeOfCare;
	private String approvalCode;
	private String diagnosis;
	private DateDto approvalStartDate;
	private DateDto approvalEndDate;
	private Long schemePlanId;
	private String nhisNumber;
	private String treatmentType;
}
