package com.hmis.server.hmis.modules.billing.dto;

import java.util.Arrays;

public enum PaymentTypeForEnum {
	SERVICE,
	DRUG,
	DEPOSIT;

	public static PaymentTypeForEnum getPaymentTypeForEnum(String value) {
		return Arrays.stream(values()).filter(v -> v.equals(PaymentTypeForEnum.valueOf(value))).findFirst().orElse(null);
	}
}
