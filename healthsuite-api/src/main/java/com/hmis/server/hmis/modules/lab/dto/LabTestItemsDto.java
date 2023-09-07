package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabTestItemsDto {
    private Long id;
    private String acknowledgeSpecimen;
    private String requestNumber;
    private LabSpecimenDto specimenDto;
    private Boolean specimenStatus;
    private String testName;

    public LabTestItemsDto(Long id, String requestNumber, String testName) {
        this.id = id;
        this.requestNumber = requestNumber;
        this.testName = testName;
    }
}
