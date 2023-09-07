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
public class AbdomenFormDto {
    private Long id;
    private YesNoEnum movesWithRespiration;
    private YesNoEnum distended;
    private YesNoEnum visiblePeripheralVein;
    private YesNoEnum scarificationMarks;
    private String shape;
    private YesNoEnum hanialOrificesIntact;
    private YesNoEnum palpationLiverEnlargement;
    private YesNoEnum palpationKidneyEnlargement;
    private YesNoEnum palpationSpleenEnlargement;
    private YesNoEnum palpationOtherMasses;
    private YesNoEnum ascultations;
    private YesNoEnum ascitis;
}
