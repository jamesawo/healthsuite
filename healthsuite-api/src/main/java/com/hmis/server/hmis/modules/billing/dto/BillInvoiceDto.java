package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BillInvoiceDto {

	private PatientDetailDto patientDetailDto;
	private String invoiceNumber;
	private DateDto costDate;
	private UserDto costBy;
	private PatientBillDto billDetails;
	private byte[] bytes;

	public BillInvoiceDto(byte[] bytes, String invoiceNumber){
		this.bytes = bytes;
		this.invoiceNumber = invoiceNumber;
	}

	public BillInvoiceDto(byte[] bytes){
		this.bytes = bytes;
	}

}
