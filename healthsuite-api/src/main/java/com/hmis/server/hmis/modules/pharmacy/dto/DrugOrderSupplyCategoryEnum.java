package com.hmis.server.hmis.modules.pharmacy.dto;

import java.util.Arrays;

public enum DrugOrderSupplyCategoryEnum {
	MOU,
	EMERGENCY,
	CREDIT,
	CASH;

	public static DrugOrderSupplyCategoryEnum getEnum(String label) {
		return Arrays.stream(values()).filter(value -> value.name().equals(label)).findFirst().orElse(null);
	}
}
