package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientAppointmentSetupDto {
	private Long id;
	@NonNull
	private Long consultantId;
	@NonNull
	private Long specialityUnitId;
	@NonNull
	private int staffLimit;

}
