package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ProductServiceDto {
	private Long id;
	@NotBlank( message = "service name is required" )
	private String name;
	private String code;
	@NotBlank
	private Double costPrice;
	@NotBlank
	private Double unitCost;
	@NotBlank
	private Double regularSellingPrice;
	@NotBlank
	private Double nhisSellingPrice;
	@NotBlank
	private Double discount;
	@NotBlank
	private Boolean applyDiscount;
	@NotBlank
	private ServiceUsageEnum usedFor;
	private Long revenueDepartmentId;
	private Long departmentId;
	private Long schemeId;
	private double schemePrice = 0;

	public ProductServiceDto( Long id, @NotBlank( message = "service name is required" ) String name ) {
		this.id = id;
		this.name = name;
	}
}
