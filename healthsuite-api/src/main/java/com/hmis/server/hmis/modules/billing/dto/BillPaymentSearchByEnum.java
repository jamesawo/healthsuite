package com.hmis.server.hmis.modules.billing.dto;

/*
	Used in bill payment module, to identify how patient bills should be searched
	*BILL NUMBER: search for patient bill using the bill number
	*PATIENT: search for patient bill using patient id
 */
public enum BillPaymentSearchByEnum {
	BILL_NUMBER("BILL_NUMBER"),
	PATIENT("PATIENT");

	public String label;
	BillPaymentSearchByEnum(String label){
		this.label = label;
	}
}
