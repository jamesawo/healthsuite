package com.hmis.server.hmis.modules.billing.dto;

/*
	Used before creating patient bill to determine how bill is created,
	NEW_BILL: create new bill
	DOCTOR_REQUEST: create new bill from doctors request during consultation
	NEW_PRESCRIPTION create pharmacy bill
 */
public enum BillSearchTypeEnum {
	NEW_BILL,
	DOCTOR_REQUEST,
	NEW_PRESCRIPTION,
	DOCTOR_PRESCRIPTION,
}
