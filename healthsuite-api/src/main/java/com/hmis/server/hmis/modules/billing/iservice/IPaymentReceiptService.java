package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientDepositLog;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;

public interface IPaymentReceiptService {
	byte[] generateBillInvoice(PatientBill savedBill, PatientCategoryEnum billPatientCategory);

	byte[] generateServiceBillReceipt(PatientPayment patientPayment);

	byte[] generatePharmacyBillReceipt(PatientPayment payment);

	byte[] generatePaymentReceipt(Long paymentId);

	//byte[] generatePaymentReceipt(PatientPayment payment, PaymentTypeForEnum typeForEnum);

	// byte[] generateDepositPaymentReceipt(PatientDepositLog depositLog, String receiptNumber);
}
