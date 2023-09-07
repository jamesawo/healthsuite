package com.hmis.server.hmis.modules.reports.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.WardDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchAdmissionDto {
    private String admittedDate;
    private String admittedTime;
    private String dischargedTime;
    private String dischargedDate;
    private PatientDetailDto patient;
    private Boolean isOnAdmission;
    private String admissionNumber;
    private WardDto ward;
    private String bed;
    private Double netAmount;
}
