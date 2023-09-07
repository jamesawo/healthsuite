package com.hmis.server.hmis.modules.pharmacy.dto;

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
public class ReceivedNoteItemDto {
	private String serialNumber;
	private String expiryDate;
	private String batchNumber;
	private String description;
	private String unitOfIssue;
	private String qtySupplied;
	private String rate;
	private String amount;
}
