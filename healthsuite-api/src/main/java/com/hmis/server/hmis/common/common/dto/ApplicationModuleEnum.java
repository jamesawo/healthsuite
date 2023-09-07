package com.hmis.server.hmis.common.common.dto;

import java.util.Arrays;

public enum ApplicationModuleEnum {
	EMR ("EMR"),
	PHARMACY ("Pharmacy"),
	BILLING ("Billing"),
	SHIFT_MANAGEMENT ("ShiftManagement"),
	SETTINGS ("Settings"),
	NURSING ("Nurses"),
	DOCTOR ("Doctor"),
	LAB ("LAB"),
	RADIOLOGY ("Radiology"),
	OTHER ("Other"),
	SETTLEMENT ("Settlement");

	public final String label;
	ApplicationModuleEnum(String label) {
		this.label = label;
	}
	public static ApplicationModuleEnum getEnum(String label) {
		return Arrays.stream(values()).filter(value -> value.name().equals(label)).findFirst().orElse(null);
	}
}
