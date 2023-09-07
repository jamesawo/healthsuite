package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabParameterSetupDto {
    private Long id;
    private ProductServiceDto test;
    private LabSpecimenDto specimen;
    private String specimenColor;
    private UserDto capturedBy;
    private DepartmentDto capturedFrom;
    private List<LabParameterSetupItemDto> parameterSetupItems;
    private Boolean isRequirePathologist;
    private Boolean isParasitologTest;
    private Boolean isSpecialTest;
    private Boolean isImmunoTest;
    private Boolean isBoneMarrowTest;
    private Boolean isHistopathologySFA;
    private LabDepartmentTypeEnum departmentTypeEnum;
}
