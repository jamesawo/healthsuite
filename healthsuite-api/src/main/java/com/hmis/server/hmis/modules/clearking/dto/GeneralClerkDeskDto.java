package com.hmis.server.hmis.modules.clearking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.SpecialityUnitDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class GeneralClerkDeskDto {
    private Long id;
    private PatientDetailDto patient;
    private UserDto clerkedBy;
    private DepartmentDto location;
    private UserDto consultant;
    private SpecialityUnitDto specialityUnit;
    private Boolean hasInformantDetail;
    private InformantDetailsDto informantDetails;
    private String followUpNote;
    private BackgroundHistoryFormDto backgroundHistory;
    private ActualDiagnosisFormDto actualDiagnosis;
    private ClerkDoctorRequestDto doctorRequest;
    private String saveAsTemplateName;
    private Boolean isSaveAsTemplate;
    private String provisionalDiagnosis;
}
