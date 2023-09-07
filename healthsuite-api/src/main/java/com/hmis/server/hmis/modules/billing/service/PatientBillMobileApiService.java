package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.PaymentMethodServiceImpl;
import com.hmis.server.hmis.common.constant.PaymentMethodEnum;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.BillPatientTypeEnum;
import com.hmis.server.hmis.modules.billing.dto.BillPaymentOptionTypeEnum;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.shift.dto.CashierShiftSetterDto;
import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.hmis.server.hmis.common.constant.HmisConstant.MOBILE_MONEY_DEPARTMENT;


@Service
@Slf4j
@RequiredArgsConstructor
public class PatientBillMobileApiService {
	private final BillServiceImpl billService;
	private final PatientPaymentServiceImpl patientPaymentService;
	private final PaymentMethodServiceImpl paymentMethodService;
	private final PatientAdmissionServiceImpl admissionService;
	private final CashierShiftServiceImpl cashierShiftService;
	private final DepartmentServiceImpl departmentService;
	private final UserServiceImpl userService;
	private final PasswordEncoder passwordEncoder;


	public ResponseEntity<PatientBillDto> findBillForMobileApi( String invoiceNumber, String basicAuth ) {
		this.verifyUser( basicAuth );
		PatientBill billModel = this.billService.findOneByBillNumber( invoiceNumber );
		PatientBillDto bill = this.billService.mapPatientBillToDto( billModel );
		return ResponseEntity.status( HttpStatus.OK ).body( bill );
	}

	@Transactional
	public ResponseEntity<Boolean> setBillIsPaidByMobileApi( String invoiceNumber, String basicAuth ) {
		User user = this.verifyUser( basicAuth );
		try {
			Department department = this.departmentService.findByName( MOBILE_MONEY_DEPARTMENT );
			PatientPayment patientPayment = this.savePatientPaymentForApi( invoiceNumber, department, user );
			this.billService.setBillIsPaidStatus( true, patientPayment, department.getName() );
			return ResponseEntity.ok( true );
		} catch ( Exception e ) {
			String message = "";
			message = e.getMessage() != null ? e.getMessage() : e.getLocalizedMessage();
			throw new RuntimeException( message );
		}
	}

	private User verifyUser( String auth ) {
		try {
			String decode = new String( Base64.decodeBase64( auth.substring( "Basic ".length() ).getBytes() ) );
			String[] split = decode.split( ":" );
			String username = split[ 0 ];
			String password = split[ 1 ];
			User user = this.userService.findByUsername( username );
			if ( !this.isPasswordMatch( user, password ) ) {
				throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Invalid Auth Credentials" );
			}
			return user;
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, e.getMessage() );
		}
	}

	private boolean isPasswordMatch( User user, String password ) {
		return this.passwordEncoder.matches( password, user.getPassword() );
	}

	private PatientPayment savePatientPaymentForApi( String invoiceNumber, Department location, User user ) {
		PatientBill bill = this.billService.findOneByBillNumber( invoiceNumber );
		if ( bill.getIsPaid() ) {
			throw new RuntimeException( invoiceNumber + " has already been paid" );
		}
		BillPatientTypeEnum billPatientTypeEnum = BillPatientTypeEnum.valueOf( bill.getBillPatientType() );
		PatientPayment payment = new PatientPayment();
		payment.setBillPaymentOptionTypeEnum( BillPaymentOptionTypeEnum.BILLED_ITEM );
		payment.setReceiptNumber( this.billService.createPaymentReceiptNumber( bill.getBillTypeEnum() ) );
		payment.setGrossTotal( bill.getGrossTotal() );
		payment.setDiscountTotal( bill.getDiscountTotal() );
		payment.setWaivedTotal( 0.00 );
		payment.setDepositAllocatedTotal( 0.00 );
		payment.setNetTotal( bill.getNetTotal() );
		payment.setPatientBill( bill );
		payment.setLocation( location );
		payment.setCashier( user );
		payment.setPaymentMethod( this.paymentMethodService.findByName( PaymentMethodEnum.MOBILE_MONEY.title() ) );
		payment.setPaymentTypeForEnum( bill.getBillTypeEnum().name() );
		payment.setCashierShift( this.cashierShiftService.findAndIncrementReceiptCount( new CashierShiftSetterDto( location, user ) ) );
		payment.setDepositLog( null );
		if ( billPatientTypeEnum == BillPatientTypeEnum.REGISTERED ) {
			Optional<PatientAdmission> optional = this.admissionService.findActiveAdmissionByPatientId( bill.getPatient().getId() );
			payment.setAdmission( optional.orElse( null ) );
		}

		return this.patientPaymentService.savePatientPayment( payment );
	}

}
