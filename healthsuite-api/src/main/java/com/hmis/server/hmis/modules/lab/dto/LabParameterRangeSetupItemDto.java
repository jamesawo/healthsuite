package com.hmis.server.hmis.modules.lab.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabParameterRangeSetupItemDto {
    private Long id;
    private String name;
    private String unit;
    private Double lowerLimit;
    private Double upperLimit;
    private Double value;

    //not in use
    private LabParameterRangeSetupDto rangeSetup;

    public LabParameterRangeSetupItemDto(Long id, String name, String unit, Double lowerLimit, Double upperLimit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }
}
