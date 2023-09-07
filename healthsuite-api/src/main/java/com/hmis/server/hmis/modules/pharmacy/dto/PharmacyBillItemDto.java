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
public class PharmacyBillItemDto {
	private Long id;
	private Boolean isPayCash;
	private Double nhisPrice;
	private Double nhisPercent;
	private int dosage;
	private String frequency;
	private int days;
	private int availableQuantity;
	private int quantity;
	private Double price;
	private Double netAmount;
	private Double grossAmount;
	private Double discountAmount;
	private DrugRegisterDto drugRegister;
	private Boolean isAllocate = false;
	private Boolean isAdjusted = false;
	private Double newQuantity;
	private String description;
}
