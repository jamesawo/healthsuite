package com.hmis.server.hmis.modules.clearking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.SpecialityUnitDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkDoctorRequest;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;


@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class OutPatientDeskDto {
    private Long id;
    private BackgroundHistoryFormDto backgroundHistory;
    private ClinicalAssessmentFormDto clinicalAssessment;
    private OtherInformationDto otherInformation;
    private SpecialityUnitDto specialityUnit;
    private PatientDetailDto patient;
    private InformantDetailsDto informantDetail;
    private PhysicalExaminationFormDto physicalExamination;
    private SystemicExaminationFormDto systemicExamination;
    private CardioVascularFormDto cardioVascularForm;
    private AbdomenFormDto abdomenForm;
    private PerieneumFormDto perieneumForm;
    private MusculoSkeletalFormDto musculoSkeletalForm;
    private NeurologicalExaminationDto neurologicalExamination;
    private ActualDiagnosisFormDto actualDiagnosisForm;
    private Boolean hasInformantDetails;
    private UserDto capturedBy;
    private DepartmentDto captureFromLocation;
    private Boolean isSaveAsTemplate;
    private String templateName;
    private ClerkDoctorRequestDto doctorRequest;
}
