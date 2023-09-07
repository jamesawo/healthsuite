package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabSpecimenCollectionDto {
    private Long id;
    private PatientDetailDto patient;
    private LabSampleSearchedByEnum searchByEnum;
    private NewOrEditSampleEnum newOrEditSampleEnum;
    private DepartmentDto capturedFrom;
    private UserDto capturedBy;
    private Boolean isAcknowledgeSpecimen;
    private String otherInformation;
    private String clinicalSummary;
    private String provisionalDiagnosis;
    private LabBillTestRequestDto labBillTestRequest;
}
