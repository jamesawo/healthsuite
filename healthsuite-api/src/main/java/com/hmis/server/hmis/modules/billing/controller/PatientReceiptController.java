package com.hmis.server.hmis.modules.billing.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.billing.dto.CancelReceiptDto;
import com.hmis.server.hmis.modules.billing.dto.SearchPaymentReceiptDto;
import com.hmis.server.hmis.modules.billing.service.BillPaymentServiceImpl;
import com.hmis.server.hmis.modules.billing.service.PaymentReceiptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX +"/receipt")
public class PatientReceiptController {
	@Autowired
	private PaymentReceiptServiceImpl paymentReceiptService;

	@GetMapping( "search-patient-receipt" )
	public List< SearchPaymentReceiptDto > searchReceipt(
			@RequestParam( value = "search" ) String search,
			@RequestParam( value = "loadPatientDetail" ) boolean loadPatientDetail,
			@RequestParam( value = "loadPatientBill" ) boolean loadPatientBill,
			@RequestParam( value = "searchBy" ) String searchBy,
			@RequestParam( value = "filterFor" ) String filterFor) {
			return this.paymentReceiptService.searchPaymentReceipt(
					search, searchBy, loadPatientDetail, loadPatientBill, filterFor);
	}

	@PostMapping(value = "cancel-payment-receipt")
	public ResponseEntity<MessageDto> cancelAReceipt(@RequestBody CancelReceiptDto dto){
		return this.paymentReceiptService.cancelPatientPayment(dto);
	}

}
