package com.hmis.server.hmis.modules.reports.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientInterimSearchDto {
    private AdmissionSessionTypeEnum visitType;
    private DateDto startDate;
    private DateDto endDate;
    private PatientDetailDto patient;
    private String admissionCode;
}
