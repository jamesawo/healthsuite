package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.PaymentMethodDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BillPaymentDto {
	private Long id;
	@NotBlank
	private String billItemPaymentType;
	@NotBlank
	private String billSearchedBy;
	private Boolean isAllocateToAllBill;
	private PatientDetailDto patientDetailPayload;
	private PatientBillDto patientBill;
	private PaymentMethodDto paymentMethod;
	private DepartmentDto department;
	private UserDto user;
	private DepositSumDto depositPayload;
	private WalkInPatientDto walkInPatient;
	private PaymentTypeForEnum paymentTypeForEnum;
}
