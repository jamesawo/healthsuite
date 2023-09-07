package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationMapDto {

    private Optional<String> departmentName;

    private Optional<String> departmentCode;

    private Optional<LocationObject> location;


//    private Optional<DepartmentDto> department;
//
//    private Optional<ApplicationModuleDto> applicationModule

}
