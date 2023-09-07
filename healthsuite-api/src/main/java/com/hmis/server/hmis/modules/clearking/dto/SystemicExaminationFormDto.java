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
public class SystemicExaminationFormDto {
    private Long id;
    private YesNoEnum dyspnoea;
    private YesNoEnum paroxysmalNocturnalDyspnoea;
    private String positionOfTrachea;
    private YesNoEnum percussionNote;
    private String respiratoryNote;
    private YesNoEnum orthepnoea;
    private YesNoEnum chestMovement;
    private YesNoEnum auscultation;
}
