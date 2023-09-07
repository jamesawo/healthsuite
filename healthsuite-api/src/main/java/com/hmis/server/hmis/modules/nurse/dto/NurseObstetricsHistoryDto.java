package com.hmis.server.hmis.modules.nurse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.nurse.model.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class NurseObstetricsHistoryDto {
    private PatientDetailDto patient;
    private NurseObGeneralForm generalForm;
    private NurseObPreviousMedical previousMedicalHistory;
    private NurseObFamilyHistory familyHistory;
    private List<NurseObPrevPregnancy> previousPregnancies;
    private NurseObHisOfPresentPregnancy historyOfPresentPregnancy;
    private NurseObPhysicalExam physicalExamination;
    private NurseObMeasurement measurement;
    private NurseObDeliveryInstruction deliveryInstruction;
    private UserDto clerkedBy;
    private DepartmentDto location;

}
