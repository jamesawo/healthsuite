package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.modules.billing.dto.*;
import java.util.List;

public interface IBillPaymentService {

	BillInvoiceDto createPatientBill(PatientBillDto billDto);

	PaymentReceiptDto handlePatientDeposit(DepositDto depositDto);

	List< PatientBillDto > searchPatientBill(String searchTerm, BillPaymentSearchByEnum searchBy, boolean loadDeposit);

	SearchPatientBillDto groupPatientBillWithDeposit(String search, BillPaymentSearchByEnum searchBy, boolean loadDeposit);

	PaymentReceiptDto makePayment(BillPaymentDto paymentDto);
}
