package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class SearchPaymentReceiptDto {
	private Long receiptId;
	private String receiptNumber;
	private PatientDetailDto patientDetail;
	private PatientBillDto patientBill;
	private Boolean isUsed;
	private Boolean isTouched;
	private DateDto transactionDate;
	private WalkInPatientDto walkInPatient;
	private String receiptPatientType;
	private String receiptBillType;
	private String receiptPaymentTypeFor;
	private Double depositAmount;

}
