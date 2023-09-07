package com.hmis.server.hmis.modules.nurse.dto;

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
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class PatientFluidBalanceDto {
    private Long id;
    private String inputType;
    private Double blood;
    private Double tube;
    private Double oral;
    private Double iv;
    private Double totalIntake;
    private Double balance;
    private Double totalOutput;
    private Double urine;
    private Double tubeVomit;
    private Double drainFaeces;
    private String outputType;
    private PatientDetailDto patient;
    private UserDto captureBy;
    private String captureByName;
    private String captureDate;
    private DepartmentDto capturedFrom;
    private String additionalInformation;

}
