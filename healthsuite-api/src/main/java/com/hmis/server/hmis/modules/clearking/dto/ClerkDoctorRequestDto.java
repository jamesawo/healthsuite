package com.hmis.server.hmis.modules.clearking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrug;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestLab;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestRadiology;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ClerkDoctorRequestDto {
    private Long id;
    private PatientDetailDto patient;
    private UserDto doctor;
    private DepartmentDto department;
    private LocalDate date = LocalDate.now();
    private ClerkRequestDrug drugRequest;
    private ClerkRequestLab labRequest;
    private ClerkRequestRadiology radiologyRequest;
}
