package com.hmis.server.hmis.modules.billing.dto;

import java.util.Arrays;

public enum BillPaymentOptionTypeEnum {
	BILLED_ITEM("BILLED_ITEM"),
	UN_BILLED_ITEM("UN-BILLED ITEM"),
	WALK_IN("WALK_IN");

	public String label;
	BillPaymentOptionTypeEnum(String label){
		this.label = label;
	}

	public static BillPaymentOptionTypeEnum getBillPaymentOption(String label) {
		return Arrays.stream(values()).filter(value -> value.label.equals(label)).findFirst().orElse(null);
	}

}
