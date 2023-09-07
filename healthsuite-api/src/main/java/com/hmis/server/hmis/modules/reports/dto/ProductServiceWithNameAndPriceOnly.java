package com.hmis.server.hmis.modules.reports.dto;

import lombok.Data;

@Data
public class ProductServiceWithNameAndPriceOnly {
	private String serviceName;
	private double generalPrice;
	private double nhisPrice;
}
