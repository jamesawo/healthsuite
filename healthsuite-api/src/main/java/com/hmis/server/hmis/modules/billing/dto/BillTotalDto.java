package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BillTotalDto {

	private Long id;

	@NotNull
	private Double grossTotal;

	@NotNull
	private Double netTotal;

	@NotNull
	private Double discountTotal;

	private Double billWaivedAmount = 0.0;
	private Double allocatedAmount = 0.0;

	public BillTotalDto(@NotNull Double grossTotal, @NotNull Double netTotal, @NotNull Double discountTotal) {
		this.grossTotal = grossTotal;
		this.netTotal = netTotal;
		this.discountTotal = discountTotal;
	}
}
