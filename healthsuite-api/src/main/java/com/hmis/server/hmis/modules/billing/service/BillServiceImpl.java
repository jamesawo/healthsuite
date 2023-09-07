package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.GenderDto;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.common.service.PaymentMethodServiceImpl;
import com.hmis.server.hmis.common.socket.SockAsyncService;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.*;
import com.hmis.server.hmis.modules.billing.iservice.IBillService;
import com.hmis.server.hmis.modules.billing.model.*;
import com.hmis.server.hmis.modules.billing.repository.PatientBillRepository;
import com.hmis.server.hmis.modules.clearking.service.DoctorRequestServiceImpl;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.dto.PatientContactDetailDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyBillItemDto;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.pharmacy.service.PharmacyBillItemServiceImpl;
import com.hmis.server.hmis.modules.shift.dto.CashierShiftSetterDto;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BillServiceImpl implements IBillService {
	private final PatientBillRepository billRepository;
	private final PatientDetailServiceImpl patientDetailService;
	private final WalkInPatientServiceImpl walkInPatientService;
	private final UserServiceImpl userService;
	private final HmisUtilService utilService;
	private final DepartmentServiceImpl locationService;
	private final InvoiceServiceImpl invoiceNumberService;
	private final BillItemServiceImpl billItemService;
	private final SchemeBillServiceImpl schemeBillService;
	private final PaymentReceiptServiceImpl receiptService;
	private final PatientAdmissionServiceImpl admissionService;
	private final DepositServiceImpl depositService;
	private final PatientPaymentServiceImpl patientPaymentService;
	private final PaymentMethodServiceImpl paymentMethodService;
	private final PharmacyBillItemServiceImpl pharmacyBillItemService;
	private final SockAsyncService asyncService;
	private final DoctorRequestServiceImpl doctorRequestService;
	private final CashierShiftServiceImpl cashierShiftService;

	@Autowired
	public BillServiceImpl(
			PatientBillRepository billRepository,
			PaymentMethodServiceImpl paymentMethodService,
			PharmacyBillItemServiceImpl pharmacyBillItemService,
			@Lazy SockAsyncService asyncService,
			PatientDetailServiceImpl patientDetailService,
			WalkInPatientServiceImpl walkInPatientService,
			UserServiceImpl userService,
			HmisUtilService utilService,
			DepartmentServiceImpl locationService,
			InvoiceServiceImpl invoiceNumberService,
			BillItemServiceImpl billItemService,
			SchemeBillServiceImpl schemeBillService,
			@Lazy PaymentReceiptServiceImpl receiptService,
			@Lazy PatientAdmissionServiceImpl admissionService,
			@Lazy DepositServiceImpl depositService,
			@Lazy PatientPaymentServiceImpl patientPaymentService,
			DoctorRequestServiceImpl doctorRequestService,
			@Lazy CashierShiftServiceImpl cashierShiftService ) {
		this.paymentMethodService = paymentMethodService;
		this.pharmacyBillItemService = pharmacyBillItemService;
		this.billRepository = billRepository;
		this.patientDetailService = patientDetailService;
		this.walkInPatientService = walkInPatientService;
		this.userService = userService;
		this.utilService = utilService;
		this.locationService = locationService;
		this.invoiceNumberService = invoiceNumberService;
		this.billItemService = billItemService;
		this.schemeBillService = schemeBillService;
		this.receiptService = receiptService;
		this.admissionService = admissionService;
		this.depositService = depositService;
		this.patientPaymentService = patientPaymentService;
		this.asyncService = asyncService;
		this.doctorRequestService = doctorRequestService;
		this.cashierShiftService = cashierShiftService;
	}


	@Override
	public PatientBill findById( Long id ) {
		Optional<PatientBill> patientBill = this.billRepository.findById( id );
		if ( !patientBill.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "CANNOT FIND PATIENT BILL" );
		}
		return patientBill.get();
	}

	public double getPatientBillTotalByAdmissionSession( PatientAdmission admission ) {
		double netAmount = 0;
		List<PatientBill> billList = this.billRepository.findAllByPatientAdmission( admission );
		if ( !billList.isEmpty() ) {
			netAmount = billList.stream().mapToDouble( PatientBill::getNetTotal ).sum();
		}
		return netAmount;
	}

	@Override
	public PatientBill findOneByBillNumber( String billNumber ) {
		Optional<PatientBill> optionalBill = this.billRepository.findByInvoiceNumber( billNumber );
		if ( !optionalBill.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "INVALID BILL NUMBER" );
		}
		return optionalBill.get();
	}

	@Override
	public BillInvoiceDto createBill( PatientBillDto dto ) {
		ValidationResponse validBillRequest = this.isValidBillRequest( dto );
		if ( !validBillRequest.getStatus() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, validBillRequest.getMessages().toString() );
		}
		List<PatientServiceBillItem> patientServiceBillItems = null;
		List<PharmacyBillItem> pharmacyBillItems = null;
		PatientBill savedBill = null;
		SchemeBill schemeBill = null;
		try {
			PatientBill patientBill = new PatientBill();
			this.mapPatientBillDtoToModel( dto, patientBill );
			// generate invoice number
			String invoiceNumber = this.invoiceNumberService.generatePatientServiceBillInvoiceNumber();
			//save bill
			patientBill.setInvoiceNumber( invoiceNumber );
			// checks if patient is on admission then set bill admission record
			this.setBillAdmissionStatus( patientBill );

			savedBill = this.billRepository.save( patientBill );
			//save bill items
			if ( dto.getBillTypeFor() == PaymentTypeForEnum.SERVICE ) {
				patientServiceBillItems = this.billItemService.createManyBillItem( dto.getBillItems(), savedBill );
			} else if ( dto.getBillTypeFor() == PaymentTypeForEnum.DRUG ) {
				pharmacyBillItems = this.pharmacyBillItemService.createMany( dto.getPharmacyBillItems(), savedBill );
			}

			if ( dto.getBillPatientType() == BillPatientTypeEnum.REGISTERED && dto.getBillPatientCategory() == PatientCategoryEnum.SCHEME ) {
				schemeBill = this.addBillToScheme( savedBill, dto.getBillPatientCategory() );
			}

			byte[] bytes = this.receiptService.generateBillInvoice( savedBill, dto.getBillPatientCategory() );
			return new BillInvoiceDto( bytes, invoiceNumber );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			if ( schemeBill != null ) {
				this.schemeBillService.removeBill( schemeBill );
			}
			if ( patientServiceBillItems != null && patientServiceBillItems.size() > 0 ) {
				this.billItemService.removeBills( patientServiceBillItems );
			}
			if ( pharmacyBillItems != null && pharmacyBillItems.size() > 0 ) {
				this.pharmacyBillItemService.removeMany( pharmacyBillItems );
			}
			if ( savedBill != null ) {
				this.removeBill( savedBill );
			}
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public boolean isBillRegisteredPatientType( PatientBillDto dto ) {
		return dto.getBillPatientType() == BillPatientTypeEnum.REGISTERED;
	}

	@Override
	public boolean isBillForSchemePatient( PatientCategoryEnum billCategoryType, Long id ) {
		if ( id != null ) {
			return this.findById( id ).getPatientCategoryEnum() == PatientCategoryEnum.SCHEME;
		} else {
			return billCategoryType == PatientCategoryEnum.SCHEME;
		}
	}

	@Override
	public SchemeBill addBillToScheme( PatientBill savedBill, PatientCategoryEnum categoryEnum ) {
		SchemeBill schemeBill = null;
		if ( categoryEnum == PatientCategoryEnum.SCHEME ) {
			schemeBill = this.schemeBillService.addBillToScheme( savedBill );
		}
		return schemeBill;
	}

	@Override
	public List<PatientBillDto> searchPatientBills( String searchTerm, BillPaymentSearchByEnum searchBy, boolean loadDeposit ) {
		try {
			if ( searchBy == BillPaymentSearchByEnum.BILL_NUMBER ) {
				return this.findBillsByBillNumber( searchTerm, loadDeposit );
			} else if ( searchBy == BillPaymentSearchByEnum.PATIENT ) {
				PatientDetail patient = this.patientDetailService.findPatientDetailById( Long.valueOf( searchTerm ) );
				return this.findBillsByPatient( patient, loadDeposit );
			} else {
				return new ArrayList<>();
			}
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public SearchPatientBillDto findPatientBillsWithDepositSum( String searchTerm, BillPaymentSearchByEnum searchBy ) {
		SearchPatientBillDto searchPatientBillDto = new SearchPatientBillDto();

		if ( searchBy == BillPaymentSearchByEnum.PATIENT ) {
			PatientDetail patient = this.patientDetailService.findPatientDetailById( Long.valueOf( searchTerm ) );
			DepositSumDto depositSum = this.depositService.findPatientDepositSum( patient );
			searchPatientBillDto.setDepositAmount( Optional.ofNullable( depositSum.getTotalAmount() ) );
		} else if ( searchBy == BillPaymentSearchByEnum.BILL_NUMBER ) {
			Optional<PatientBill> serviceBill = this.billRepository.findByInvoiceNumber( searchTerm );
			if ( serviceBill.isPresent() ) {
				DepositSumDto depositSum = this.depositService.findPatientDepositSum( serviceBill.get().getPatient() );
				searchPatientBillDto.setDepositAmount( Optional.ofNullable( depositSum.getTotalAmount() ) );
			}
		}
		List<PatientBillDto> bills = this.searchPatientBills( searchTerm, searchBy, false );
		searchPatientBillDto.setBillDtoList( bills );
		return searchPatientBillDto;
	}

	@Override
	public List<PatientBillDto> findBillsByBillNumber( String invoiceNumber, boolean loadDeposit ) {
		List<PatientBillDto> dtoList = new ArrayList<>();
		List<PatientBill> serviceBills = this.billRepository.findAllByInvoiceNumberIsLikeIgnoreCaseAndIsPaid( invoiceNumber, false );
		return getPatientBillDtos( loadDeposit, dtoList, serviceBills );
	}

	@Override
	public List<PatientBillDto> findBillsByPatient( PatientDetail patientDetail, boolean loadDeposit ) {
		List<PatientBillDto> dtoList = new ArrayList<>();
		//		List< PatientBill > serviceBills = this.billRepository.findAllByPatient(patientDetail);
		List<PatientBill> serviceBills = this.billRepository.findAllByPatientAndIsPaid( patientDetail, false );
		return getPatientBillDtos( loadDeposit, dtoList, serviceBills );
	}

	@Override
	public List<BillItemDto> getServiceBillItemsFromBill( PatientBill serviceBill ) {
		List<BillItemDto> itemDtoList = new ArrayList<>();
		List<PatientServiceBillItem> billItems = serviceBill.getPatientServiceBillItems();
		if ( billItems != null && billItems.size() > 0 ) {
			itemDtoList = billItems.stream().map( this::mapBillItemToDto ).collect( Collectors.toList() );
		}
		return itemDtoList;
	}

	@Override
	@Transactional
	public PaymentReceiptDto makeBillPayment( BillPaymentDto paymentDto ) {
		try {
			this.validatePaymentReceipt( paymentDto );
			this.handlePaymentDepositAllocation( paymentDto ); // todo refactor handlePaymentDepositAllocation
			// save payment record
			PatientPayment mappedToModel = this.mapPaymentDtoToModel( paymentDto );
			PatientPayment patientPayment = this.patientPaymentService.savePatientPayment( mappedToModel );
			// update bill is paid
			this.setBillIsPaidStatus( true, patientPayment, paymentDto.getDepartment().getName().get() );
			// generate receipt
			byte[] bytes = this.receiptService.generatePaymentReceipt( patientPayment.getId() );
			return new PaymentReceiptDto( bytes );
		} catch ( Exception e ) {
			// throw runtime exception to enable persistent rollback  (if any error occurred)
			log.error( e.getMessage(), e );
			throw new RuntimeException( e.getMessage() );
		}
	}

	@Override
	public PatientBillDto mapServiceBillToDto( PatientBill serviceBill ) {
		PatientBillDto dto = new PatientBillDto();
		dto.setInvoiceNumber( serviceBill.getInvoiceNumber() );
		dto.setId( serviceBill.getId() );

		dto.setBillPatientCategory( serviceBill.getPatientCategoryEnum() );
		dto.setBillPatientType( BillPatientTypeEnum.valueOf( serviceBill.getBillPatientType() ) );
		dto.setBillSearchType( BillSearchTypeEnum.valueOf( serviceBill.getBillSearchType() ) );

		dto.setBillItems( this.getServiceBillItemsFromBill( serviceBill ) ); // set service bill items
		dto.setPharmacyBillItems( this.pharmacyBillItemService.getPharmacyBillItemsFromBill( serviceBill ) ); //set pharmacy bill items

		dto.setBillTotal( new BillTotalDto( serviceBill.getGrossTotal(), serviceBill.getNetTotal(), serviceBill.getDiscountTotal() ) );
		if ( serviceBill.getBillPatientType().equals( BillPatientTypeEnum.REGISTERED.label ) ) {
			dto.setPatientId( serviceBill.getPatient().getId() );
			dto.setPatientDetailDto( serviceBill.getPatient().transformToDto() );
		} else if ( serviceBill.getBillPatientType().equals( BillPatientTypeEnum.WALK_IN.label ) && serviceBill.getWalkInPatient() != null ) {
			dto.setPatientDetailDto( this.setPatientDetailFromWalkInPatient( serviceBill.getWalkInPatient() ) );
			dto.setWalkInPatient( new WalkInPatientDto( serviceBill.getWalkInPatient().getId(), serviceBill.getWalkInPatient().getFirstName(), serviceBill.getWalkInPatient().getLastName(),
					serviceBill.getWalkInPatient().getOtherName() ) );
		} else {
			dto.setPatientDetailDto( new PatientDetailDto() );
		}

		dto.setCostDate( this.utilService.transformToDateDto( serviceBill.getCostDate() ) );
		dto.setCostByDto( new UserDto( Optional.of( serviceBill.getCostBy().getId() ), Optional.of( serviceBill.getCostBy().getFullName() ) ) );
		dto.setLocationDto( new DepartmentDto( serviceBill.getLocation().getId(), serviceBill.getLocation().getName() ) );
		//set the bill payment type (drug or service)
		if ( serviceBill.getBillTypeEnum() != null ) {
			dto.setBillTypeFor( serviceBill.getBillTypeEnum() );
		}
		return dto;
	}

	@Override
	public PatientBillDto mapPatientBillToDto( PatientBill model ) {
		PatientBillDto dto = new PatientBillDto();
		dto.setId( model.getId() );
		PaymentTypeForEnum paymentTypeForEnum = model.getBillTypeEnum();
		dto.setBillTypeFor( paymentTypeForEnum );

		dto.setBillPatientCategory( model.getPatientCategoryEnum() );
		dto.setBillSearchType( BillSearchTypeEnum.valueOf( model.getBillSearchType() ) );
		BillPatientTypeEnum billPatientTypeEnum = BillPatientTypeEnum.getBillPatientTypeEnum( model.getBillPatientType() );
		dto.setBillPatientType( billPatientTypeEnum );

		dto.setBillTotal( new BillTotalDto( model.getGrossTotal(), model.getNetTotal(), model.getDiscountTotal() ) );
		dto.setCostById( model.getCostBy().getId() );
		dto.setCostByDto( this.userService.mapToDtoClean( model.getCostBy() ) );
		dto.setLocationId( model.getLocation().getId() );
		dto.setLocationDto( this.locationService.mapModelToDto( model.getLocation() ) );
		dto.setCostDate( this.utilService.transformToDateDto( model.getCostDate() ) );
		if ( billPatientTypeEnum == BillPatientTypeEnum.REGISTERED ) {
			dto.setPatientDetailDto( model.getPatient().transformToDto() );
		} else if ( billPatientTypeEnum == BillPatientTypeEnum.WALK_IN ) {
			dto.setWalkInPatient( this.walkInPatientService.mapToDto( model.getWalkInPatient() ) );
		}
		dto.setIsCredit( model.getIsCredit() );
		dto.setInvoiceNumber( model.getInvoiceNumber() );

		if ( paymentTypeForEnum == PaymentTypeForEnum.SERVICE ) {
			dto.setBillItems( this.getServiceBillItemsFromBill( model ) );
		} else if ( paymentTypeForEnum == PaymentTypeForEnum.DRUG ) {
			dto.setPharmacyBillItems( this.pharmacyBillItemService.getPharmacyBillItemsFromBill( model ) );
		}
		return dto;
	}

	@Override
	public double getBillTotalByRevenueDepartment( RevenueDepartment revenueDepartment, PatientBill bill ) {
		double total = 0;
		if ( bill != null ) {
			PaymentTypeForEnum type = bill.getBillTypeEnum();
			if ( type == PaymentTypeForEnum.SERVICE ) {
				total = this.getServiceBillTotalForRevenueDepartment( revenueDepartment, bill.getPatientServiceBillItems() );
			} else if ( type == PaymentTypeForEnum.DRUG ) {
				total = this.getDrugBillTotalByRevenueDepartment( revenueDepartment, bill.getPharmacyBillItems() );
			}
		}
		return total;
	}

	public double getBillTotalByServiceDepartment( Department serviceDepartment, PatientBill bill ) {
		double total = 0;
		if ( bill != null ) {
			PaymentTypeForEnum type = bill.getBillTypeEnum();
			if ( type == PaymentTypeForEnum.SERVICE ) {
				if ( !bill.getPatientServiceBillItems().isEmpty() ) {
					for ( PatientServiceBillItem item : bill.getPatientServiceBillItems() ) {
						if ( item.getProductService().getDepartment().getId().equals( serviceDepartment.getId() ) ) {
							total += item.getNetAmount();
						}
					}
				}
			}
		}
		return total;
	}

	// used in patient revisit view under account summary details
	public PatientBillStatsDto getPatientAccountSummaryStats( long patientId ) {
		// todo:: write function to get patient total unpaid bill amount, patient net bill amount
		try {
			PatientDetail patient = this.patientDetailService.findPatientDetailById( patientId );
			DepositSumDto depositSum = this.depositService.findPatientDepositSum( patient );
			if ( depositSum != null ) {
				return new PatientBillStatsDto();
			}
			return new PatientBillStatsDto( depositSum.getTotalAmount() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	public void handleCancelPatientBill( PatientBill bill ) {
		PaymentTypeForEnum billTypeEnum = bill.getBillTypeEnum();
		if ( billTypeEnum.equals( PaymentTypeForEnum.SERVICE ) ) {
			this.handleCancelPatientServiceBill( bill );
		} else if ( billTypeEnum.equals( PaymentTypeForEnum.DRUG ) ) {
			this.handleCancelPatientDrugBill( bill );
		}
	}

	public void handleCancelPatientServiceBill( PatientBill bill ) {
		List<PatientServiceBillItem> serviceBillItems = bill.getPatientServiceBillItems();
		if ( !serviceBillItems.isEmpty() ) {
			for ( PatientServiceBillItem item : serviceBillItems ) {
				this.billItemService.handleCancelServiceBillItem( item );
			}
		}
	}

	public void handleCancelPatientDrugBill( PatientBill bill ) {
		List<PharmacyBillItem> pharmacyBillItems = bill.getPharmacyBillItems();
		if ( !pharmacyBillItems.isEmpty() ) {
			for ( PharmacyBillItem item : pharmacyBillItems ) {
				this.pharmacyBillItemService.handleCancelDrugBillItem( item );
			}
		}
	}

	public void saveAdjustedBill(
			PatientBill bill,
			PatientAdjustedBill adjustedBill,
			double grossTotal,
			double netTotal,
			double discountTotal
	) {
		bill.setAdjustedBill( adjustedBill );
		bill.setIsAdjusted( true );
		bill.setGrossTotal( grossTotal );
		bill.setNetTotal( netTotal );
		bill.setDiscountTotal( discountTotal );
		this.billRepository.save( bill );
	}

	public void setBillIsPaidStatus( boolean status, PatientPayment payment, String cashPointName ) {
		String receiptNumber = payment.getReceiptNumber();
		this.billRepository.setBillIsPaid( status, receiptNumber, payment.getPatientBill().getId() );
		PatientBill patientBill = this.findById( payment.getPatientBill().getId() );
		//async execution
		this.asyncService.prepNurseWaitingListAfterBillPayment( patientBill, cashPointName, receiptNumber );
	}

	// get payment receipt check PaymentTypeForEnum, generate service receipt or drug receipt
	public String createPaymentReceiptNumber( PaymentTypeForEnum typeForEnum ) {
		String number = "";
		if ( typeForEnum == PaymentTypeForEnum.SERVICE ) {
			number = this.invoiceNumberService.generateServiceReceiptNumber();
		} else if ( typeForEnum == PaymentTypeForEnum.DRUG ) {
			number = this.invoiceNumberService.generatePharmacyReceiptNumber();
		}
		return number;
	}

	public BillItemDto mapBillItemToDto( PatientServiceBillItem billItem ) {
		BillItemDto billItemDto = new BillItemDto();
		billItemDto.setId( billItem.getId() );
		billItemDto.setQuantity( billItem.getQuantity() );
		billItemDto.setPrice( billItem.getPrice() );
		billItemDto.setGrossAmount( billItem.getGross() );
		billItemDto.setPayCash( billItem.getPayCash() );
		billItemDto.setNhisPrice( billItem.getNhisPrice() );
		billItemDto.setNhisPercent( billItem.getNhisPercent() );
		billItemDto.setNetAmount( billItem.getNetAmount() );
		billItemDto.setDiscountAmount( billItem.getDiscountAmount() );
		billItemDto.setProductService( new ProductServiceDto( billItem.getProductService().getId(), billItem.getProductService().getName() ) );
		billItemDto.setDescription( billItem.getProductService().getName() );
		return billItemDto;
	}

	/* before saving a patient bill, check if the patient is on admission and then update the bill admission column
	(used for getting all bills on patient admission session) */
	private void setBillAdmissionStatus( PatientBill bill ) {
		if ( bill.getPatient() != null && bill.getPatient().getId() != null ) {
			boolean isOnAdmission = this.admissionService.isPatientOnAdmission( bill.getPatient().getId() );
			if ( isOnAdmission ) {
				bill.setIsOnAdmission( true );
				bill.setPatientAdmission( this.admissionService.findPatientAdmissionRaw( bill.getPatient().getId() ) );
			}
		}
	}

	private double getServiceBillTotalForRevenueDepartment( RevenueDepartment revenueDepartment, List<PatientServiceBillItem> billItems ) {
		double total = 0;
		if ( !billItems.isEmpty() ) {
			for ( PatientServiceBillItem item : billItems ) {
				if ( item.getProductService().getRevenueDepartment().getId().equals( revenueDepartment.getId() ) ) {
					total += item.getNetAmount();
				}
			}
		}
		return total;
	}

	private double getDrugBillTotalByRevenueDepartment( RevenueDepartment revenueDepartment, List<PharmacyBillItem> billItems ) {
		double total = 0;
		if ( !billItems.isEmpty() ) {
			for ( PharmacyBillItem item : billItems ) {
				if ( item.getDrugRegister().getRevenueDepartment().getId().equals( revenueDepartment.getId() ) ) {
					total += item.getNetAmount();
				}
			}
		}
		return total;
	}

	private List<PatientBillDto> getPatientBillDtos( boolean loadDeposit, List<PatientBillDto> dtoList, List<PatientBill> serviceBills ) {
		if ( serviceBills.size() > 0 ) {
			List<PatientBillDto> list = new ArrayList<>();
			for ( PatientBill serviceBill : serviceBills ) {
				PatientBillDto patientBillDto = this.mapServiceBillToDto( serviceBill );
				if ( loadDeposit && patientBillDto.getPatientId() != null ) {
					DepositSumDto depositSum = this.depositService.findPatientDepositSum( new PatientDetail( patientBillDto.getPatientId() ) );
					patientBillDto.setDeposit( depositSum );
				}
				list.add( patientBillDto );
			}
			dtoList = list;
		}
		return dtoList;
	}

	private PatientPayment mapPaymentDtoToModel( BillPaymentDto dto ) {
		PatientPayment model = new PatientPayment();
		BillPaymentOptionTypeEnum option = BillPaymentOptionTypeEnum.getBillPaymentOption( dto.getBillItemPaymentType() );
		model.setBillPaymentOptionTypeEnum( option );
		model.setWaivedTotal( dto.getPatientBill().getBillTotal().getBillWaivedAmount() != null ? dto.getPatientBill().getBillTotal().getBillWaivedAmount() : 0.00 );
		//set patient bill
		model.setPatientBill( this.setPatientBillBeforePayment( dto ) );
		// todo:: recalculate bill totals from bill item
		// this.calculateAndSetPatientBillTotalAmount(model, dto);
		model.setGrossTotal( dto.getPatientBill().getBillTotal().getGrossTotal() );
		model.setDiscountTotal( dto.getPatientBill().getBillTotal().getDiscountTotal() );
		model.setDepositAllocatedTotal( dto.getPatientBill().getBillTotal().getAllocatedAmount() );
		model.setNetTotal( dto.getPatientBill().getBillTotal().getNetTotal() );

		model.setLocation( this.locationService.findOneRaw( dto.getDepartment().getId() ) );
		model.setPaymentMethod( this.paymentMethodService.findOneRaw( dto.getPaymentMethod().getId().get() ) );
		model.setCashier( this.userService.findOneRaw( dto.getUser().getId().get() ) );
		model.setDate( LocalDate.now() );
		model.setTime( LocalTime.now() );
		model.setPaymentTypeForEnum( dto.getPaymentTypeForEnum().name() );
		// get cashier current shift number or create one
		model.setCashierShift( this.getCashierShiftCodeAndIncrementReceiptCount( dto.getUser(), dto.getDepartment() ) );
		model.setReceiptNumber( this.createPaymentReceiptNumber( dto.getPaymentTypeForEnum() ) );
		// check if patient is on admission then add admission details to payment
		if ( dto.getPatientDetailPayload() != null && dto.getPatientDetailPayload().getPatientId() != null ) {
			Optional<PatientAdmission> optionalAdmission = this.admissionService.findActiveAdmissionByPatientId( dto.getPatientDetailPayload().getPatientId() );
			optionalAdmission.ifPresent( model::setAdmission );
		}
		return model;
	}

	private CashierShift getCashierShiftCodeAndIncrementReceiptCount( UserDto user, DepartmentDto location ) {
		User cashier = this.userService.findOneFromDto( user );
		Department department = this.locationService.findOneFromDto( location );
		return this.cashierShiftService.findAndIncrementReceiptCount( new CashierShiftSetterDto( department, cashier ) );
	}

	// set PatientPayment patientBill property before saving payment
	private PatientBill setPatientBillBeforePayment( BillPaymentDto billDto ) {
		PatientBillDto dto = billDto.getPatientBill();
		PatientBill patientBill = new PatientBill();
		if ( billDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.WALK_IN.label ) ) {
			dto.setWalkInPatient( billDto.getWalkInPatient() );
			dto.setBillPatientType( BillPatientTypeEnum.WALK_IN );
			patientBill.setInvoiceNumber( this.invoiceNumberService.generatePatientServiceBillInvoiceNumber() );

			dto.setBillTypeFor( billDto.getPaymentTypeForEnum() );
			dto.setCostByDto( billDto.getUser() );
			dto.setCostById( billDto.getUser().getId().get() );
			dto.setLocationDto( billDto.getDepartment() );
			dto.setLocationId( billDto.getDepartment().getId().get() );
			dto.setBillPatientType( BillPatientTypeEnum.valueOf( billDto.getBillItemPaymentType() ) );
			this.mapPatientBillDtoToModel( dto, patientBill );
			patientBill = this.billRepository.save( patientBill );
			this.billItemService.createManyBillItem( dto.getBillItems(), patientBill );
		} else if ( billDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.UN_BILLED_ITEM.label ) ) {
			dto.setBillPatientType( BillPatientTypeEnum.REGISTERED );
			dto.setPatientId( billDto.getPatientDetailPayload().getPatientId() );
			dto.setCostById( billDto.getUser().getId().get() );
			dto.setLocationId( billDto.getDepartment().getId().get() );
			dto.setBillTypeFor( billDto.getPaymentTypeForEnum() );
			patientBill = this.setPatientBillForUnBilled( billDto );
		} else if ( billDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.BILLED_ITEM.label ) ) {
			patientBill = this.findOneByBillNumber( dto.getInvoiceNumber() );
		}
		return patientBill;
	}

	private PatientBill setPatientBillForUnBilled( BillPaymentDto billPaymentDto ) {
		PatientBillDto dto = billPaymentDto.getPatientBill();
		PatientBill bill = new PatientBill();
		bill.setBillTypeEnum( dto.getBillTypeFor() );
		if ( dto.getBillPatientType().label.equals( BillPatientTypeEnum.REGISTERED.label ) ) {
			PatientDetail patient = this.patientDetailService.findPatientDetailById( dto.getPatientId() );
			bill.setPatient( patient );
			bill.setPatientCategoryEnum( patient.getPatientCategory() );
		} else if ( dto.getBillPatientType().label.equals( BillPatientTypeEnum.WALK_IN.label ) ) {
			bill.setWalkInPatient( this.walkInPatientService.createWalkInPatient( dto.getWalkInPatient() ) );
		}
		bill.setBillSearchType( dto.getBillSearchType().name() );
		bill.setBillPatientType( dto.getBillPatientType().label );
		bill.setGrossTotal( dto.getBillTotal().getGrossTotal() );
		bill.setNetTotal( dto.getBillTotal().getNetTotal() );
		bill.setDiscountTotal( dto.getBillTotal().getDiscountTotal() );
		bill.setCostBy( this.userService.findOneRaw( dto.getCostById() ) );
		bill.setLocation( this.locationService.findOneRaw( Optional.of( dto.getLocationId() ) ) );
		bill.setCostDate( LocalDateTime.now() );
		bill.setInvoiceNumber( this.invoiceNumberService.generatePatientServiceBillInvoiceNumber() );
		bill = this.billRepository.save( bill );
		//save bill items
		if ( dto.getBillTypeFor() == PaymentTypeForEnum.SERVICE ) {
			this.billItemService.createManyBillItem( dto.getBillItems(), bill );
		}
		return bill;
	}

	// handle deposit bill allocation, in payment manager for bills that are checked to be settled from deposit
	private void handlePaymentDepositAllocation( BillPaymentDto paymentDto ) {
		// todo update PatientDepositHistory if bills are allocated
		if ( !paymentDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.WALK_IN.label ) ) {
			if ( !paymentDto.getPatientBill().getBillPatientType().label.equals( BillPatientTypeEnum.WALK_IN.label ) ) {
				// todo recalculate deposit from bill
				Double allocatedAmount = paymentDto.getPatientBill().getBillTotal().getAllocatedAmount();
				PatientDetail patientDetail = this.patientDetailService.findPatientDetailById( paymentDto.getPatientDetailPayload().getPatientId() );
				// settle deposit (reduce the total bill allocated amount from deposit balance)
				if ( this.hasBillAllocation( allocatedAmount, paymentDto.getPatientBill().getBillItems() ) ) {
					this.settleBillAllocation( patientDetail, allocatedAmount );
				}
			}

		}
	}

	// check patient payment before saving
	private void validatePaymentReceipt( BillPaymentDto paymentDto ) {
		ValidationResponse isValidBillPayment = this.validatePaymentBill( paymentDto );
		if ( isValidBillPayment.getStatus().equals( false ) ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, isValidBillPayment.getMessages().toString() );
		}
	}

	// check if payment has an bill allocation from deposit balance
	private boolean hasBillAllocation( Double totalAmountAllocated, List<BillItemDto> billItems ) {
		// ... check total allocated amount from bill total
		return totalAmountAllocated != null && totalAmountAllocated > 0;
	}

	// settle bill allocation by reducing the allocated bill total from deposit balance
	private void settleBillAllocation( PatientDetail patientDetail, Double totalBillAllocated ) {
		// check patient deposit sum
		Double depositAmount = this.depositService.findPatientDepositAmount( patientDetail );
		if ( depositAmount < totalBillAllocated ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "INSUFFICIENT DEPOSIT BALANCE" );
		}
		this.depositService.settleFromDepositSum( totalBillAllocated, patientDetail );
	}

	private void mapPatientBillDtoToModel( PatientBillDto dto, PatientBill model ) {
		//set patient bill type
		if ( dto.getBillPatientType() != null ) {
			model.setBillPatientType( dto.getBillPatientType().label );
			if ( dto.getBillPatientType() == BillPatientTypeEnum.REGISTERED && dto.getPatientId() != null ) {
				PatientDetail patient = this.patientDetailService.findPatientDetailById( dto.getPatientId() );
				model.setPatient( patient );
				model.setPatientCategoryEnum( patient.getPatientCategory() );
			} else {
				model.setWalkInPatient( this.walkInPatientService.createWalkInPatient( dto.getWalkInPatient() ) );
			}
		}
		//set bill patient category
		if ( dto.getBillPatientCategory() != null ) {
			model.setPatientCategoryEnum( dto.getBillPatientCategory() );
		}
		// set bill search type
		if ( dto.getBillSearchType() != null ) {
			model.setBillSearchType( dto.getBillSearchType().name() );
		}

		//todo set bill total amount
		if ( dto.getBillTotal() != null ) {
			if ( dto.getBillTotal().getNetTotal() != null ) {
				model.setNetTotal( dto.getBillTotal().getNetTotal() );
			}
			if ( dto.getBillTotal().getDiscountTotal() != null ) {
				model.setDiscountTotal( dto.getBillTotal().getDiscountTotal() );
			}
			if ( dto.getBillTotal().getGrossTotal() != null ) {
				model.setGrossTotal( dto.getBillTotal().getGrossTotal() );
			}

            /* wip
            model.setNetTotal(this.getTotalAmountFromServiceBillDtoBySumTypeEnum(SumTypeEnum.NET,dto));
            model.setDiscountTotal(this.getTotalAmountFromServiceBillDtoBySumTypeEnum(SumTypeEnum.DISCOUNT,dto));
            model.setGrossTotal(this.getTotalAmountFromServiceBillDtoBySumTypeEnum(SumTypeEnum.GROSS,dto));
            */
		}

		// set user that created the bill
		if ( dto.getCostById() != null ) {
			model.setCostBy( this.userService.findOneRaw( dto.getCostById() ) );
		}
		// set bill date ( date this bill is created)
		if ( dto.getCostDate() != null ) {
			model.setCostDate( this.utilService.transformToLocalDateTime( dto.getCostDate() ) );
		} else {
			model.setCostDate( LocalDateTime.now() );
		}
		// set location where bill was created
		if ( dto.getLocationId() != null ) {
			model.setLocation( this.locationService.findOneRaw( Optional.of( dto.getLocationId() ) ) );
		}
		//set isCredit // todo::refactor set isCredit
		if ( dto.getIsCredit() != null ) {
			model.setIsCredit( dto.getIsCredit() );
		} else { //check if patient is on admission but also check if is credit bill is set from client
			model.setIsCredit( this.admissionService.isPatientOnAdmission( dto.getPatientId() ) );
		}

		if ( dto.getBillTypeFor() != null ) {
			model.setBillTypeEnum( dto.getBillTypeFor() );
		}

		if ( dto.getIsDoctorRequest() && dto.getDoctorRequest().getId() != null ) {
			model.setIsDoctorRequest( dto.getIsDoctorRequest() );
			model.setDoctorRequest( this.doctorRequestService.findById( dto.getDoctorRequest().getId() ) );
		}
	}

	private PatientDetailDto setPatientDetailFromWalkInPatient( WalkInPatient walkInPatient ) {
		PatientDetailDto patientDetailDto = new PatientDetailDto();
		String fullName = "";
		if ( walkInPatient.getFirstName() != null ) {
			patientDetailDto.setPatientFirstName( walkInPatient.getFirstName() );
			fullName = walkInPatient.getFirstName();
		}
		if ( walkInPatient.getLastName() != null ) {
			patientDetailDto.setPatientLastName( walkInPatient.getLastName() );
			fullName = fullName + " " + walkInPatient.getLastName();
		}
		if ( walkInPatient.getOtherName() != null ) {
			patientDetailDto.setPatientOtherName( walkInPatient.getOtherName() );
			fullName = fullName + " " + walkInPatient.getOtherName();
		}
		if ( walkInPatient.getAge() != null ) {
			patientDetailDto.setPatientAge( walkInPatient.getAge() );
		}
		if ( walkInPatient.getGender() != null ) {
			patientDetailDto.setGenderDto( new GenderDto( Optional.ofNullable( walkInPatient.getGender().getId() ), Optional.ofNullable( walkInPatient.getGender().getName() ) ) );
		}
		if ( walkInPatient.getPhone() != null ) {
			patientDetailDto.setPatientContactDetail( new PatientContactDetailDto( walkInPatient.getPhone() ) );
		}
		patientDetailDto.setPatientFullName( fullName );
		return patientDetailDto;
	}

	private ValidationResponse isValidBillRequest( PatientBillDto dto ) {
		ValidationResponse response = new ValidationResponse( true );
		if ( dto.getBillPatientType() == null ) {
			response.setStatus( false );
			response.addMessage( "Patient type is required" );
		}

		if ( dto.getBillTypeFor() == PaymentTypeForEnum.SERVICE ) {
			if ( dto.getBillItems().size() < 1 ) {
				response.setStatus( false );
				response.addMessage( "Add at least one bill item" );
			} else {
				this.removeZeroNetAmountFromServiceBillItem( dto );
				if ( dto.getBillItems().size() < 1 ) {
					response.setStatus( false );
					response.addMessage( "No Valid Bill Item Found" );
				}
			}
		}

		if ( dto.getBillTypeFor() == PaymentTypeForEnum.DRUG ) {
			if ( dto.getPharmacyBillItems().size() < 1 ) {
				response.setStatus( false );
				response.addMessage( "Add at least one bill item" );
			} else {
				this.removeZeroNetAmountFromPharmBills( dto );
				if ( dto.getPharmacyBillItems().size() < 1 ) {
					response.setStatus( false );
					response.addMessage( "No Valid Bill Item Found" );
				}
			}
		}

		if ( dto.getCostById() == null ) {
			response.setStatus( false );
			response.addMessage( "Cashier cannot be empty" );
		}

		if ( dto.getBillTotal() == null ) {
			response.setStatus( false );
			response.addMessage( "Invalid bill amount" );
		}
		return response;
	}

	private void removeZeroNetAmountFromPharmBills( PatientBillDto dto ) {
		List<PharmacyBillItemDto> itemDtoList = dto.getPharmacyBillItems();
		if ( itemDtoList != null && itemDtoList.size() > 0 ) {
			Iterator<PharmacyBillItemDto> iterator = itemDtoList.iterator();
			while ( iterator.hasNext() ) {
				PharmacyBillItemDto nextDto = ( PharmacyBillItemDto ) iterator.next();
				if ( nextDto.getNetAmount() < 1 ) {
					iterator.remove();
				}
			}
		}
	}

	// delete a bill if error occurred during saving bill and generating invoice (clean up)
	private void removeBill( PatientBill savedBill ) {
		this.billRepository.delete( savedBill );
	}

	// remove bill items with zero net amount
	private void removeZeroNetAmountFromServiceBillItem( PatientBillDto dto ) {
		List<BillItemDto> billItemDtoList = dto.getBillItems();
		if ( billItemDtoList != null && billItemDtoList.size() > 0 ) {
			Iterator iterator = billItemDtoList.iterator();
			while ( iterator.hasNext() ) {
				BillItemDto nextDto = ( BillItemDto ) iterator.next();
				if ( nextDto.getNetAmount() < 1 ) {
					iterator.remove();
				}
			}
		}
	}

	// check if a bill has been paid
	private boolean isBillInvoicePaid( String invoiceNumber ) {
		Optional<PatientBill> bill = this.billRepository.findByInvoiceNumber( invoiceNumber );
		if ( !bill.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "INVALID BILL NUMBER" );
		}
		return bill.get().getIsPaid();
	}

	// validate bill from payment manager before saving and generating receipt
	private ValidationResponse validatePaymentBill( BillPaymentDto paymentDto ) {
		ValidationResponse validationResponse = new ValidationResponse( true );
		if ( paymentDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.BILLED_ITEM.label ) ) {

			if ( paymentDto.getPatientBill().getInvoiceNumber() != null ) {
				boolean isBillPaid = this.isBillInvoicePaid( paymentDto.getPatientBill().getInvoiceNumber() );
				if ( isBillPaid ) {
					validationResponse.addMessage( "BILL HAS BEEN PAID." );
					validationResponse.setStatus( false );
					return validationResponse;
				}
			}

			if ( paymentDto.getPatientBill().getBillPatientType() == BillPatientTypeEnum.REGISTERED ) {
				if ( paymentDto.getPatientDetailPayload() == null || paymentDto.getPatientDetailPayload().getPatientId() == null ) {
					validationResponse.addMessage( "Patient Is Required." );
					validationResponse.setStatus( false );
				}
			} else if ( paymentDto.getPatientBill().getBillPatientType() == BillPatientTypeEnum.WALK_IN ) {
				if ( paymentDto.getWalkInPatient() == null || paymentDto.getWalkInPatient().getId() == null ) {
					validationResponse.addMessage( "WalkInPatient From Bill Is Required." );
					validationResponse.setStatus( false );
				}
			}


			if ( paymentDto.getPatientBill() == null ) {
				validationResponse.addMessage( "Bill Is Required For Billed Item." );
				validationResponse.setStatus( false );
			}

			// check if bill has been paid

		} else if ( paymentDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.UN_BILLED_ITEM.label ) ) {
			if ( paymentDto.getPatientDetailPayload() == null || paymentDto.getPatientDetailPayload().getPatientId() == null ) {
				validationResponse.addMessage( "Patient Is Required" );
				validationResponse.setStatus( false );
			}
			if ( paymentDto.getPatientBill() == null ) {
				validationResponse.addMessage( "Bill Is Required For UnBilled Item." );
				validationResponse.setStatus( false );
			}

		} else if ( paymentDto.getBillItemPaymentType().equals( BillPaymentOptionTypeEnum.WALK_IN.label ) ) {
			if ( paymentDto.getWalkInPatient() == null ) {
				validationResponse.addMessage( "WalkIn Patient Details Is Required." );
				validationResponse.setStatus( false );
			}
		}
		if ( paymentDto.getPatientBill().getBillTypeFor() == PaymentTypeForEnum.SERVICE ) {
			if ( paymentDto.getPatientBill() == null || paymentDto.getPatientBill().getBillItems().size() < 1 ) {
				validationResponse.addMessage( "Service Bill Items Cannot Be Empty" );
				validationResponse.setStatus( false );
			}
		} else if ( paymentDto.getPatientBill().getBillTypeFor() == PaymentTypeForEnum.DRUG ) {
			if ( paymentDto.getPatientBill() == null || paymentDto.getPatientBill().getPharmacyBillItems().size() < 1 ) {
				validationResponse.addMessage( "Drug Bill Items Cannot Be Empty" );
				validationResponse.setStatus( false );
			}
		}
		return validationResponse;
	}

}
