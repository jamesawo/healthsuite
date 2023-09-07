package com.hmis.server.hmis.modules.billing.dto;

import java.util.Arrays;

public enum BillPatientTypeEnum {
	WALK_IN("WALK_IN"),
	REGISTERED("REGISTERED");
	public final String label;

	BillPatientTypeEnum(String label) {
		this.label = label;
	}

	public static BillPatientTypeEnum getBillPatientTypeEnum(String label) {
		return Arrays.stream(values()).filter(value -> value.label.equals(label)).findFirst().orElse(null);
	}
}
