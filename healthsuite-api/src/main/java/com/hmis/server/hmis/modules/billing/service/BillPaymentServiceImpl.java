package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.modules.billing.dto.*;
import com.hmis.server.hmis.modules.billing.iservice.IBillPaymentService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BillPaymentServiceImpl implements IBillPaymentService {

	private final BillServiceImpl billService;
	private final DepositServiceImpl depositService;

	@Autowired
	public BillPaymentServiceImpl(BillServiceImpl billService, DepositServiceImpl depositService) {
		this.billService = billService;
		this.depositService = depositService;
	}

	@Override
	public BillInvoiceDto createPatientBill(PatientBillDto billDto) {
		return this.billService.createBill(billDto);
	}

	@Override
	public PaymentReceiptDto handlePatientDeposit(DepositDto depositDto) {
		return this.depositService.handlePatientDepositPayment(depositDto);
	}

	@Override
	public List< PatientBillDto > searchPatientBill(String searchTerm, BillPaymentSearchByEnum searchBy, boolean loadDeposit) {
		return this.billService.searchPatientBills(searchTerm, searchBy, loadDeposit);
	}

	@Override
	public SearchPatientBillDto groupPatientBillWithDeposit(String search, BillPaymentSearchByEnum searchBy, boolean loadDeposit) {
		return billService.findPatientBillsWithDepositSum(search, searchBy);
	}

	@Override
	public PaymentReceiptDto makePayment(BillPaymentDto paymentDto) {
		return this.billService.makeBillPayment(paymentDto);
	}
}
