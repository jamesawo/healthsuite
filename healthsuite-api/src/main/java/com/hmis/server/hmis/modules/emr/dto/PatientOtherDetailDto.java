package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientOtherDetailDto {
    private Optional<Long> id;
    private Boolean accessToTelephone;
    private Boolean accessToInternet;
    private Boolean accessToElectricity;
    private Boolean accessToCleanWater;
    private Boolean accessToGoodFood;
    private Boolean accessToGoodRoad;
}
