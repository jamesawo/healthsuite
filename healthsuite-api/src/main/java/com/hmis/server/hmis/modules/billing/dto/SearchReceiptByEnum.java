package com.hmis.server.hmis.modules.billing.dto;

import java.util.Arrays;

public enum SearchReceiptByEnum {
	RECEIPT_NUMBER,
	PATIENT_NUMBER;

	public static SearchReceiptByEnum getSearchReceiptByEnum(String value){
		return Arrays.stream(values()).filter(v->v.equals(SearchReceiptByEnum.valueOf(value))).findFirst().orElse(null);
	}
}
