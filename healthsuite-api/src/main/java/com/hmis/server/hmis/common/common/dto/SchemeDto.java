package com.hmis.server.hmis.common.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class SchemeDto {
	private Long id;
	private String insuranceName;
	private String phoneNumber;
	private String postalAddress;
	private String insuranceCode;
	private String emailAddress;
	private String addressLineTitle;
	private int discount;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String label;
	private String organizationType;
	private List<SchemePlanDto> schemePlans;
}
