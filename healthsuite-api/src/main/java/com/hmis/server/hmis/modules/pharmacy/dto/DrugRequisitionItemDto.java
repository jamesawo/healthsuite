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
public class DrugRequisitionItemDto {
	private Long id;
	private DrugRegisterDto drugRegister;
	private Integer requestingQuantity;
	private Integer unitOfIssue;
	private Integer issuingOutletBalance;
	private DrugRequisitionDto drugRequisition;
	private Integer issuingQuantity;
}
