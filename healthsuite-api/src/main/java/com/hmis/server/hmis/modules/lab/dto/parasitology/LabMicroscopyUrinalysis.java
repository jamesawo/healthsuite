package com.hmis.server.hmis.modules.lab.dto.parasitology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true ) @JsonInclude( JsonInclude.Include.NON_NULL )
public class LabMicroscopyUrinalysis {
    private String morphology;
    private String reaction;
    private String wbc;
    private String rbc;
    private String cast;
    private String epithelials;
    private String ph;
    private String appearance;
    private String ketone;
    private String urobilinogen;
    private String nitrite;
    private String trichomonasVaginalis;
    private String blood;
    private String glucose;
    private String specificGravity;
    private String crystal;
    private String bilirubin;
    private String ascrobicAcid;
    private String bacteria;
    private String protein;
    private String yeast;
    private String leucocyte;
    private String other;
}
