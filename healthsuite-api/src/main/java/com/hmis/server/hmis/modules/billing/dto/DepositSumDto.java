package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString @NoArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DepositSumDto {
	private Long id;
	private Double totalAmount;

	public DepositSumDto(Long id, Double totalAmount) {
		this.id = id;
		this.totalAmount = totalAmount;
	}
}
