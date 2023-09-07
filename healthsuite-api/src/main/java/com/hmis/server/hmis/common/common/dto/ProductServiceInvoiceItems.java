package com.hmis.server.hmis.common.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductServiceInvoiceItems {
	private String productServiceName;
	private String productServiceQty;
	private String productServiceAmt;
}
