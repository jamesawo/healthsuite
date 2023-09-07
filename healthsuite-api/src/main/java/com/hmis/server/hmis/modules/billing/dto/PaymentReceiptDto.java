package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PaymentReceiptDto {
	private byte[] bytes;
	private String shiftCode;

	public PaymentReceiptDto(byte[] bytes) {
		this.bytes = bytes;
	}

	public PaymentReceiptDto(byte[] bytes, String shiftCode) {
		this.bytes = bytes;
		this.shiftCode = shiftCode;
	}
}
