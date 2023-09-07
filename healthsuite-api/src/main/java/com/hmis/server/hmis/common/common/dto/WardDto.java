package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WardDto {
    private Long id;
    private String code;
    private int totalBedCount;
    private int occupiedBedCount;
    private Long departmentId;
    List<BedDto> beds;
    DepartmentDto department;
    private int percentageCount;
    private String title;
    private int numberOfBedsToUpdate;

    private WardUpdateTypeEnum type;

    public WardDto(Long id, String code) {
        this.id = id;
        this.code = code;
    }
}
