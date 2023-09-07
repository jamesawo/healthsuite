package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BillItemDto {
	private Long id;
	private Long productServiceId;
	private String productServiceName;
	private Double quantity;
	private Double price;
	private Double grossAmount;
	private Boolean payCash;
	private Double nhisPrice;
	private Double nhisPercent;
	private Double netAmount;
	private Double discountAmount;
	private ProductServiceDto productService;
	private Double waivedAmount;
	private Boolean isAllocate = false;
	private Boolean isAdjusted = false;
	private Double newQuantity;
	private String description;

	public BillItemDto(Long id, Long productServiceId, String productServiceName) {
		this.id = id;
		this.productServiceId = productServiceId;
		this.productServiceName = productServiceName;
	}
}
