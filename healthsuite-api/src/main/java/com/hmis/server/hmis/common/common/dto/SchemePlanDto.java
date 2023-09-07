package com.hmis.server.hmis.common.common.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SchemePlanDto {
	private Long id;
	private String planType;
	private int discount;
	private double percentService;
	private double percentDrug;

	public SchemePlanDto() {
	}

	public SchemePlanDto( Long id, String planType, int discount, double percentService, double percentDrug ) {
		this.id = id;
		this.planType = planType;
		this.discount = discount;
		this.percentService = percentService;
		this.percentDrug = percentDrug;
	}
}


