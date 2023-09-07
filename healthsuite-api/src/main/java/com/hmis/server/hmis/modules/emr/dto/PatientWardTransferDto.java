package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.BedDto;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.WardDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientWardTransferDto {
    private PatientDetailDto patient;
    private WardDto newWard;
    private DateDto transferDate;
    private String transferNote;
    private BedDto newBed;
    private UserDto consultant;
    private DepartmentDto location;
    private UserDto user;
    private PatientAdmissionDto currentAdmission;
}
