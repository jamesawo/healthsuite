package com.hmis.server.hmis.modules.billing.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.service.SchemeServicePriceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.billing.dto.*;
import com.hmis.server.hmis.modules.billing.service.BillAdjustmentServiceImpl;
import com.hmis.server.hmis.modules.billing.service.BillPaymentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/billing" )
public class PatientBillingController {
	private final BillPaymentServiceImpl billPaymentServiceImpl;
	private final BillAdjustmentServiceImpl billAdjustmentService;
	private final SchemeServicePriceImpl schemeServicePrice;

	public PatientBillingController(
			BillPaymentServiceImpl billPaymentServiceImpl,
			BillAdjustmentServiceImpl billAdjustmentService,
			SchemeServicePriceImpl schemeServicePrice ) {
		this.billPaymentServiceImpl = billPaymentServiceImpl;
		this.billAdjustmentService = billAdjustmentService;
		this.schemeServicePrice = schemeServicePrice;
	}

	@PostMapping( value = "patient-billing" )
	public ResponseEntity<byte[]> patientBilling( @RequestBody @Valid PatientBillDto billDto ) {
		BillInvoiceDto patientBill = this.billPaymentServiceImpl.createPatientBill( billDto );
		return ResponseEntity.ok( patientBill.getBytes() );
	}

	@PostMapping( value = "patient-deposit" )
	public ResponseEntity<byte[]> handlePatientDeposit( @RequestBody @Valid DepositDto depositDto ) {
		PaymentReceiptDto paymentReceiptDto;
		paymentReceiptDto = this.billPaymentServiceImpl.handlePatientDeposit( depositDto );
		return ResponseEntity.ok( paymentReceiptDto.getBytes() );
	}

	@GetMapping( value = "search-patient-bill" )
	public List<PatientBillDto> searchPatientBill(
			@RequestParam( value = "search" ) String search,
			@RequestParam( value = "searchBy" ) BillPaymentSearchByEnum searchBy,
			@RequestParam( value = "loadDeposit" ) boolean loadDeposit ) {
		return this.billPaymentServiceImpl.searchPatientBill( search, searchBy, loadDeposit );
	}

	// search for patient bills with the patient deposit amount
	@GetMapping( value = "group-patient-bill-deposit" )
	public SearchPatientBillDto groupPatientBillWithDepositSum(
			@RequestParam( value = "search" ) String search,
			@RequestParam( value = "searchBy" ) BillPaymentSearchByEnum searchBy,
			@RequestParam( value = "loadDeposit" ) boolean loadDeposit ) {
		return this.billPaymentServiceImpl.groupPatientBillWithDeposit( search, searchBy, loadDeposit );
	}

	// payment manager
	@PostMapping( "make-payment" )
	public ResponseEntity<byte[]> makePayment( @RequestBody BillPaymentDto billPaymentDto ) {
		try {
			PaymentReceiptDto dto = this.billPaymentServiceImpl.makePayment( billPaymentDto );
			return ResponseEntity.ok( dto.getBytes() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@PostMapping( value = "adjust-bill" )
	public ResponseEntity<MessageDto> billAdjustment( @RequestBody PatientBillDto dto ) {
		return this.billAdjustmentService.saveAdjustedBill( dto );
	}

	@GetMapping( value = "get-nhis-service-price" )
	public ResponseEntity<Double> getNhisServicePrice(
			@RequestParam( value = "patient" ) Long patientId,
			@RequestParam( value = "service" ) Long serviceId
	) {
		Double price = this.schemeServicePrice.getSchemeServicePriceByPatient( patientId, serviceId );
		return ResponseEntity.ok().body( price );
	}
}

