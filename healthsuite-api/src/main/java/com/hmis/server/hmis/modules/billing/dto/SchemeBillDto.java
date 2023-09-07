package com.hmis.server.hmis.modules.billing.dto;

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
public class SchemeBillDto {
	private Long id;
	private Long patientBillId;
	private Long patientId;
	private double amountToPay;
	private String nhisNo;
	private String recordedDiagnosis;
	private String recordedApprovalCode;
}
