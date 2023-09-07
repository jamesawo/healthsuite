package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabTestTrackerDto {
    private Long id;
    private String billType;
    private String paymentStatus;
    private String labTestCode;
    private Boolean isSpecimenCollected = false;
    private String specimenCollectedByUser;
    private String specimenCollectedDate;

    private Boolean isSpecimenAck  = false;
    private String specimenAckByUser;
    private String specimenAckDate;
    private String specimenAckStatus;

    private Boolean isLabTestPrepared  = false;
    private String specimenPrepByUser;
    private String specimenPrepDate;

    private Boolean isLabTestVerified  = false;
    private String testVerifiedByUser;
    private String testVerifiedDate;
    private String testVerifiedApproved;

    private Boolean isPathologistVerified  = false;
    private String testVerifiedByPathologistUser;
    private String testVerifiedByPathologistDate;
    private String testVerifiedByPathologistApproved;
}
