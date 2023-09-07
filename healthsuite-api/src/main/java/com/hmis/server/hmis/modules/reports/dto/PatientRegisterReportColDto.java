package com.hmis.server.hmis.modules.reports.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.GenderDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.dto.PatientTypeEnum;
import lombok.*;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PatientRegisterReportColDto {
    private String serialNumber;
    private String patientName;
    private String patientNumber;
    private String patientDob;
    private String patientAge;
    private String patientGender;
    private String patientCategory;
    private String patientAddress;
    private String patientRegisterDate;
    private String patientFolderNumber;
    private String patientCardNumber;
}
