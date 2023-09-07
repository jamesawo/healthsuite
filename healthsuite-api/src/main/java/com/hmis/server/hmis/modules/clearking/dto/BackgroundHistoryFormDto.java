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
public class BackgroundHistoryFormDto {
    private Long id;
    private String presentingComplaint;
    private String historyOfPresentingComplaint;
    private String reviewOfSystem;
    private String pastMedicalAndSurgicalHistory;
    private String psychiatricHistory;
    private String gynaecologyHistory;
    private String paediatricHistory;
    private String drugHistory;
    private String immunizationHistory;
    private String travelHistory;
    private String familyHistory;
}
