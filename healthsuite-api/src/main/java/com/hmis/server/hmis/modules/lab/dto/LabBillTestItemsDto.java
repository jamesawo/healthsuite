package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkRequestLabDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabBillTestItemsDto {
    private Long id;
    private String requestNumber;
    private String testName;
    private LabSpecimenDto specimenDto;
    private Boolean specimenStatus;
    private String acknowledgement;
    private String collectedBy;

    public LabBillTestItemsDto(
            Long id,
            String requestNumber,
            String testName,
            LabSpecimenDto specimenDto,
            Boolean specimenStatus) {
        this.id = id;
        this.requestNumber = requestNumber;
        this.testName = testName;
        this.specimenDto = specimenDto;
        this.specimenStatus = specimenStatus;
    }

    public LabBillTestItemsDto(
            Long id,
            String requestNumber,
            String testName,
            LabSpecimenDto specimenDto,
            String acknowledgement, String collectedBy) {
        this.id = id;
        this.requestNumber = requestNumber;
        this.testName = testName;
        this.specimenDto = specimenDto;
        this.acknowledgement = acknowledgement;
        this.collectedBy = collectedBy;
    }
}
