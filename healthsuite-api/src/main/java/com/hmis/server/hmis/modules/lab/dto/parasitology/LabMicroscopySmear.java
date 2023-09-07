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
public class LabMicroscopySmear {
    private LabMicroscopySmearTypeEnum type;
    private LabMicroscopySmearGram gram;
    private LabMicroscopySmearGiemsa giemsa;
    private LabMicroscopySmearZeihlNeelsen zeihlNeelsen;
    private LabMicroscopySmearOther others;
}
