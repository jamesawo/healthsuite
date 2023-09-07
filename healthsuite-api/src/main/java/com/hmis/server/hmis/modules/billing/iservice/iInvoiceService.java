package com.hmis.server.hmis.modules.billing.iservice;

public interface iInvoiceService {

	String generatePatientServiceBillInvoiceNumber();

	String generateServiceReceiptNumber();

	String generateDepositReceiptNumber();

	String generatePharmacyReceiptNumber();
}
