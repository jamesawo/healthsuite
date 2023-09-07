package com.hmis.server.hmis.modules.clearking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class PhysicalExaminationFormDto {
    private Long id;
    private String levelOfConsciousness;
    private String patientType;
    private YesNoEnum febril;
    private YesNoEnum pallor;
    private String dehydration;
    private YesNoEnum cyanosis;
    private YesNoEnum jaundice;
}
