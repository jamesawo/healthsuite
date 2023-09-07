package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabResultTestParamDto {
    private Long testParamId; // ==> lab test request item id of LabTestRequestItem
    private String filmReport;
    private String testParamName;
    private String comment;
    private List<LabParameterRangeSetupItemDto> testRangeParamList;

    public LabResultTestParamDto(Long testParamId, List<LabParameterRangeSetupItemDto> testRangeParamList) {
        this.testParamId = testParamId;
        this.testRangeParamList = testRangeParamList;
    }

    public LabResultTestParamDto(Long testParamId, String testParamName, List<LabParameterRangeSetupItemDto> testRangeParamList) {
        this.testParamId = testParamId;
        this.testParamName = testParamName;
        this.testRangeParamList = testRangeParamList;
    }
}
