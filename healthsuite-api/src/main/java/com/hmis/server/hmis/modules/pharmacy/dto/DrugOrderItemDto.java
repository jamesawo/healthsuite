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
public class DrugOrderItemDto {
	private Long id;
	private DrugOrderDto drugOrder;
	private Integer quantity;
	private Integer rate;
	private Double totalAmount;
	private DrugRegisterDto drugRegister;
}
