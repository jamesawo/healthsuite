package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PharmacyReceivedGoodsItemDto {
	private Long id;
	private Integer quantityOrdered;
	private Integer quantityReceived;
	private Integer quantitySupplied;
	private Integer rate;
	private Integer unitOfIssue;
	private Double totalCost;
	private String batchNumber;
	private DateDto expiryDate;
	private DrugRegisterDto drugRegister;
	private PharmacyReceivedGoodsDto pharmacyReceivedGoods;
}
