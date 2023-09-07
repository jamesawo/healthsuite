package com.hmis.server.hmis.modules.lab.dto.parasitology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabMicroscopySfa {
	private String morphology;
	private String reaction;
	private String abstinence;
	private String toTest;
	private String volume;
	private String liquefaction;
	private String viscosity;
	private String ph;
	private String wbc;
	private String rbc;
	private String immatureCells;
	private String antiSpermAb;
	private String vitality;
	private String odour;
	private String appearance;
	private String wbcCone;
	private String aggultination;
	private String aggregation;
	private String acrosomeT;
	private String timeOfProduction;
	private String timeReceived;
	private String timeOfExamination;
	private String modeOfProduction;
	private LabMicroscopySfaType sfaType;
	private LabMicroscopySfaMachine machine;
	private LabMicroscopySfaManual manual;
}
