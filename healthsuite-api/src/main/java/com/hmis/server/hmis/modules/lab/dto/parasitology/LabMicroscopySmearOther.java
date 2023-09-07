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
public class LabMicroscopySmearOther {
    private String result;
    private String morphology;
    private String reaction;
    private String parasite;
    private String stage;
    private String count;
    private String wbc;
    private String rbc;
    private String cast;
    private String whiff;
    private String epithelials;
    private String fungi;
    private String bacteria;
    private String pusCell;
}
