package com.hmis.server.hmis.modules.clearking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class DoctorWaitingDto {
    private Long id;
    private Long patientId;
    private String waitingStatus;
    private String patientName;
    private String patientNumber;
    private String patientCategory;
    private Long doctorId;
    private Long clinicId;
}
