package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DepositDto {
	@NotNull
	private Long patientId;
	@NotNull
	private Long locationId;
	@NotNull
	private Long userId;
	@NotNull
	private Long revenueDepartmentId;

	private Long paymentMethodId;

	private Double depositAmount;
	@NotNull
	private String description;
	private String transactionRefNumber;

	private PatientDetailDto patientDetail;
}
