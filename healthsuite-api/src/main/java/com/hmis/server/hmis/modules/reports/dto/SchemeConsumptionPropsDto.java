package com.hmis.server.hmis.modules.reports.dto;

import lombok.Data;

@Data
public class SchemeConsumptionPropsDto {
	private String serviceTitle;
	private int quantity;
	private double price;
	private double discount;
	private String diagnosis;
	private String approvalCode;
	private String typeOfCare;
	private String date;
	// add props
	private String patientName;
	private String patientPhone;
	private String patientNumber;
	private Double hmoAmount;
	private String chargeFor; // SERVICE OR DRUG
	private String nhisNumber;
}
