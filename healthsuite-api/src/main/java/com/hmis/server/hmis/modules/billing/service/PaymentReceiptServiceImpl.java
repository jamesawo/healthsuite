package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ProductServiceInvoiceItems;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.modules.billing.dto.*;
import com.hmis.server.hmis.modules.billing.iservice.IPaymentReceiptService;
import com.hmis.server.hmis.modules.billing.model.*;
import com.hmis.server.hmis.modules.billing.repository.PaymentReceiptRepository;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.pharmacy.service.PharmacyBillItemServiceImpl;
import com.hmis.server.hmis.modules.shift.dto.CashierShiftSetterDto;
import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;

@Service
@Slf4j
public class PaymentReceiptServiceImpl implements IPaymentReceiptService {

	private final PaymentReceiptRepository receiptRepository;
	private final GlobalSettingsImpl globalSettingsService;
	private final BillItemServiceImpl billItemService;
	private final PharmacyBillItemServiceImpl pharmacyBillItemService;
	private final HmisUtilService utilService;
	private final WalkInPatientServiceImpl walkInPatientService;
	private final PatientPaymentServiceImpl paymentService;
	private final CommonService commonService;
	private final CashierShiftServiceImpl shiftService;

	@Value("${hmis.printer.size}")
	private String printerSize;

	@Autowired
	public PaymentReceiptServiceImpl(
			GlobalSettingsImpl globalSettingsService,
			PaymentReceiptRepository receiptRepository,
			BillItemServiceImpl billItemService,
			PharmacyBillItemServiceImpl pharmacyBillItemService,
			HmisUtilService utilService,
			WalkInPatientServiceImpl walkInPatientService,
			PatientPaymentServiceImpl paymentService,
			CommonService commonService,
			@Lazy CashierShiftServiceImpl shiftService) {
		this.globalSettingsService = globalSettingsService;
		this.billItemService = billItemService;
		this.receiptRepository = receiptRepository;
		this.pharmacyBillItemService = pharmacyBillItemService;
		this.utilService = utilService;
		this.walkInPatientService = walkInPatientService;
		this.paymentService = paymentService;
		this.commonService = commonService;
		this.shiftService = shiftService;
	}

	public PaymentReceipt findOneRaw(Long id) {
		Optional<PaymentReceipt> optional = this.receiptRepository.findById(id);
		if (!optional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt Not Found");
		}
		return optional.get();
	}

	@Override
	public byte[] generateServiceBillReceipt(PatientPayment patientPayment) {
		HashMap<String, Object> map = this.getServiceOrDrugReceiptMap(patientPayment);
		ReceiptTypeEnum receiptTypeEnum = ReceiptTypeEnum.SERVICE_BILL_RECEIPT;
		InputStream filePath = this.getFilePath(receiptTypeEnum);
		this.saveServiceReceiptBeforePrint(patientPayment, receiptTypeEnum);
		return this.createInvoiceByte(map, filePath);
	}

	@Override
	public byte[] generatePaymentReceipt(Long paymentId) {
		PatientPayment payment = this.paymentService.findById(paymentId);
		PaymentTypeForEnum typeForEnum = PaymentTypeForEnum.getPaymentTypeForEnum(payment.getPaymentTypeForEnum());
		if (typeForEnum == PaymentTypeForEnum.SERVICE) {
			return this.generateServiceBillReceipt(payment);
		} else if (typeForEnum == PaymentTypeForEnum.DRUG) {
			return this.generatePharmacyBillReceipt(payment);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment Type Is Invalid");
		}
	}

	public byte[] generateDepositPaymentReceipt(PatientDepositLog depositLog, String receiptNumber,
			PatientPayment patientPayment) {
		PaymentReceipt receipt = this.saveDepositReceiptBeforePrint(depositLog, receiptNumber, patientPayment);
		InputStream path = this.getFilePath(ReceiptTypeEnum.DEPOSIT_RECEIPT);
		HashMap<String, Object> depositReceiptMap = this.getDepositReceiptMap(receipt);
		return this.createInvoiceByte(depositReceiptMap, path);
	}

	@Override
	public byte[] generatePharmacyBillReceipt(PatientPayment payment) {
		HashMap<String, Object> map = this.getServiceOrDrugReceiptMap(payment);
		InputStream filePath = this.getFilePath(ReceiptTypeEnum.SERVICE_BILL_RECEIPT);
		this.saveServiceReceiptBeforePrint(payment, ReceiptTypeEnum.DRUG_BILL_RECEIPT);
		return this.createInvoiceByte(map, filePath);
	}

	@Override
	public byte[] generateBillInvoice(PatientBill savedBill, PatientCategoryEnum billPatientCategory) {
		HashMap<String, Object> map = this.getBillInvoiceMap(savedBill);
		InputStream path = null;
		if (savedBill.getBillTypeEnum() == PaymentTypeForEnum.SERVICE) {
			path = this.getFilePath(ReceiptTypeEnum.SERVICE_BILL_INVOICE);
		} else if (savedBill.getBillTypeEnum() == PaymentTypeForEnum.DRUG) {
			path = this.getFilePath(ReceiptTypeEnum.DRUG_BILL_INVOICE);
		} else {
			log.error("Cannot Generate Invoice For Bill");
			log.error("Invoice Type Is Not Defined (Hint: PaymentTypeForEnum (DRUG / SERVICE))");
		}
		return this.createInvoiceByte(map, path);
	}

	public List<SearchPaymentReceiptDto> searchPaymentReceipt(
			String term,
			String searchBy,
			boolean loadPatientDetail,
			boolean loadPatientBill, String filterFor) {
		try {
			List<SearchPaymentReceiptDto> receiptDtoList = new ArrayList<>();
			SearchReceiptByEnum searchReceiptByEnum = SearchReceiptByEnum.getSearchReceiptByEnum(searchBy);
			PaymentTypeForEnum forEnum = PaymentTypeForEnum.getPaymentTypeForEnum(filterFor);
			if (searchReceiptByEnum == SearchReceiptByEnum.PATIENT_NUMBER) {
				List<PaymentReceipt> receipts = this.receiptRepository.findByPatientDetailId(Long.valueOf(term));
				if (receipts.size() > 0) {
					receipts.forEach(receipt -> {
						if (receipt.getIsUsed().equals(false)
								&& !receipt.getReceiptStatusEnum().equals(ReceiptStatusEnum.CANCELLED)) {
							SearchPaymentReceiptDto receiptDto = this.mapPaymentReceiptToSearchPaymentReceiptDto(
									receipt, loadPatientDetail, loadPatientBill);
							receiptDtoList.add(receiptDto);
						}
					});
				}
			} else if (searchReceiptByEnum == SearchReceiptByEnum.RECEIPT_NUMBER) {
				Optional<PaymentReceipt> optional = this.receiptRepository.findByReceiptNumberEquals(term);
				if (optional.isPresent()) {
					PaymentReceipt receipt = optional.get();
					if (receipt.getIsUsed().equals(false)
							&& !receipt.getReceiptStatusEnum().equals(ReceiptStatusEnum.CANCELLED)) {
						SearchPaymentReceiptDto receiptDto = this.mapPaymentReceiptToSearchPaymentReceiptDto(receipt,
								loadPatientDetail, loadPatientBill);
						receiptDtoList.add(receiptDto);
					}
				}
			}
			return receiptDtoList;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	public boolean isValidReceipt(Long receiptId) {
		// checks if receipt has been created before and status is unused
		Optional<PaymentReceipt> optional = this.receiptRepository.findById(receiptId);
		return optional.filter(paymentReceipt -> !paymentReceipt.getIsUsed()).isPresent();
	}

	public boolean isValidReceiptFor(Long receiptId, PaymentTypeForEnum paymentTypeForEnum) {
		PaymentReceipt receipt = this.findOneRaw(receiptId);
		PaymentTypeForEnum billTypeEnum = receipt.getPayment().getPatientBill().getBillTypeEnum();
		return billTypeEnum.equals(paymentTypeForEnum);
	}

	public void setReceiptIsUsedAfterDispensing(Long receiptId) {
		this.receiptRepository.setReceiptIsUsed(receiptId, true, ReceiptStatusEnum.USED);
	}

	/*
	 * cancel patient payment; checks if receipt is drug payment then checks if
	 * drugs were returned before cancelling
	 * payment, and setting receipt status to cancel.
	 */
	public ResponseEntity<MessageDto> cancelPatientPayment(CancelReceiptDto dto) {
		this.onValidateBeforeCancelReceipt(dto);
		try {
			PaymentReceipt receipt = this.findOneRaw(dto.getPaymentReceiptId());
			ResponseEntity<MessageDto> response = this.paymentService.cancelPatientPayment(receipt, dto);
			this.cancelPaymentReceipt(receipt);
			return response;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private void cancelPaymentReceipt(PaymentReceipt receipt) {
		receipt.setReceiptStatusEnum(ReceiptStatusEnum.CANCELLED);
		receipt.setIsUsed(true);
		receipt.setIsTouched(true);
		this.receiptRepository.save(receipt);
	}

	private void onValidateBeforeCancelReceipt(CancelReceiptDto dto) {
		if (dto.getPaymentReceiptId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment is required");
		}
		if (dto.getCancelledBy() == null || !dto.getCancelledBy().getId().isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancelled By is Required");
		}
		if (dto.getCancelledFrom() == null || !dto.getCancelledFrom().getId().isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancelled From Location is Required");
		}
	}

	private SearchPaymentReceiptDto mapPaymentReceiptToSearchPaymentReceiptDto(
			PaymentReceipt paymentReceipt,
			boolean loadPatientDetail,
			boolean loadPatientBill) {
		if (paymentReceipt.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Receipt ID Details");
		}
		SearchPaymentReceiptDto dto = new SearchPaymentReceiptDto();
		dto.setReceiptId(paymentReceipt.getId());
		dto.setReceiptNumber(paymentReceipt.getReceiptNumber());
		dto.setIsUsed(paymentReceipt.getIsUsed());
		dto.setIsTouched(paymentReceipt.getIsTouched());
		dto.setTransactionDate(
				this.utilService.transformDateAndTime(paymentReceipt.getLocalDate(), paymentReceipt.getLocalTime()));
		PaymentTypeForEnum paymentTypeForEnum = PaymentTypeForEnum
				.valueOf(paymentReceipt.getPayment().getPaymentTypeForEnum());
		if (paymentTypeForEnum.equals(PaymentTypeForEnum.DEPOSIT)) {
			dto.setReceiptPaymentTypeFor(PaymentTypeForEnum.DEPOSIT.name());
			dto.setDepositAmount(paymentReceipt.getPayment().getDepositLog().getDepositAmount());
		} else if (paymentTypeForEnum.equals(PaymentTypeForEnum.SERVICE)) {
			dto.setReceiptPaymentTypeFor(PaymentTypeForEnum.SERVICE.name());
		} else if (paymentTypeForEnum.equals(PaymentTypeForEnum.DRUG)) {
			dto.setReceiptPaymentTypeFor(PaymentTypeForEnum.DRUG.name());
		}
		BillPaymentOptionTypeEnum billPaymentOptionTypeEnum = paymentReceipt.getPayment()
				.getBillPaymentOptionTypeEnum();
		dto.setReceiptBillType(billPaymentOptionTypeEnum.label);
		if (loadPatientDetail) {
			if (billPaymentOptionTypeEnum == BillPaymentOptionTypeEnum.WALK_IN) {
				dto.setWalkInPatient(this.walkInPatientService.mapToDto(paymentReceipt.getWalkInPatient()));
				dto.setReceiptPatientType(BillPatientTypeEnum.WALK_IN.label);
			} else {
				dto.setPatientDetail(paymentReceipt.getPatientDetail().transformToDto());
				dto.setReceiptPatientType(BillPatientTypeEnum.REGISTERED.label);
			}
		}

		if (loadPatientBill && paymentReceipt.getPayment().getPatientBill() != null) {
			PatientBillDto billDto = this.paymentService
					.mapPatientBillToPatientBillDto(paymentReceipt.getPayment().getPatientBill());
			billDto.getCostByDto().setRole(Optional.empty());
			billDto.getCostByDto().setExpiryDate(Optional.empty());
			dto.setPatientBill(billDto);
		}
		return dto;
	}

	private void saveServiceReceiptBeforePrint(PatientPayment patientPayment, ReceiptTypeEnum receiptTypeEnum) {
		PaymentReceipt paymentReceipt = new PaymentReceipt();
		paymentReceipt.setReceiptNumber(patientPayment.getReceiptNumber());
		paymentReceipt.setReceiptTypeEnum(receiptTypeEnum);
		paymentReceipt.setReceiptStatusEnum(ReceiptStatusEnum.ACTIVE);
		BillPaymentOptionTypeEnum billPaymentOption = BillPaymentOptionTypeEnum
				.getBillPaymentOption(patientPayment.getBillPaymentOptionTypeEnum().label);
		if (billPaymentOption == BillPaymentOptionTypeEnum.BILLED_ITEM) {
			if (patientPayment.getPatientBill().getBillPatientType().equals(BillPatientTypeEnum.REGISTERED.label)) {
				paymentReceipt
						.setPatientDetail(new PatientDetail(patientPayment.getPatientBill().getPatient().getId()));
			} else if (patientPayment.getPatientBill().getBillPatientType().equals(BillPatientTypeEnum.WALK_IN.label)) {
				paymentReceipt.setWalkInPatient(patientPayment.getPatientBill().getWalkInPatient());
			}
		} else if (billPaymentOption == BillPaymentOptionTypeEnum.UN_BILLED_ITEM) {
			paymentReceipt.setPatientDetail(new PatientDetail(patientPayment.getPatientBill().getPatient().getId()));
		} else if (billPaymentOption == BillPaymentOptionTypeEnum.WALK_IN) {
			paymentReceipt
					.setWalkInPatient(new WalkInPatient(patientPayment.getPatientBill().getWalkInPatient().getId()));
		}
		paymentReceipt.setPayment(patientPayment);

		if (patientPayment.getCashierShift() != null && patientPayment.getCashierShift().getCode() != null) {
			paymentReceipt.setShiftNumber(patientPayment.getCashierShift().getCode());
		} else {
			paymentReceipt.setShiftNumber("N/A");
		}
		this.receiptRepository.save(paymentReceipt);
	}

	private PaymentReceipt saveDepositReceiptBeforePrint(PatientDepositLog depositLog, String receiptNumber,
			PatientPayment patientPayment) {
		PaymentReceipt paymentReceipt = new PaymentReceipt();
		paymentReceipt.setReceiptNumber(receiptNumber);
		paymentReceipt.setReceiptTypeEnum(ReceiptTypeEnum.DEPOSIT_RECEIPT);
		paymentReceipt.setReceiptStatusEnum(ReceiptStatusEnum.ACTIVE);
		paymentReceipt.setPatientDetail(depositLog.getPatient());
		paymentReceipt.setShiftNumber(this.shiftService.getOrCreateShift(
				new CashierShiftSetterDto(depositLog.getDepartment(),
						depositLog.getUser()))
				.getCode());
		paymentReceipt.setPayment(patientPayment);
		return this.receiptRepository.save(paymentReceipt);
	}

	private byte[] createInvoiceByte(HashMap<String, Object> parameters, InputStream reportPath) {
		return this.commonService.generatePDFBytes(parameters, reportPath);
	}

	private HashMap<String, Object> getServiceOrDrugReceiptMap(PatientPayment payment) {
		HashMap<String, Object> hashMap = new HashMap<>();
		List<ProductServiceInvoiceItems> data = this.getBillItemsFromReceipt(payment.getPatientBill(),
				PaymentTypeForEnum.valueOf(payment.getPaymentTypeForEnum()));
		hashMap.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		hashMap.put("hmisServiceList", new JRBeanCollectionDataSource(data));
		hashMap.put("cashier", payment.getCashier().getFullName());
		hashMap.put("shiftNumber", payment.getCashierShift().getCode());
		hashMap.put("cashPoint", payment.getLocation().getName());
		hashMap.put("receiptTime", this.utilService.formatTimeToAM_PM(payment.getTime()));
		hashMap.put("receiptDate", payment.getDate().toString());
		hashMap.put("receiptNumber", payment.getReceiptNumber());
		hashMap.put("logo", this.commonService.getLogoAsStream());

		PNameDto nameAndNumber = this.getPaymentPatientNumber(payment.getPatientBill());
		hashMap.put("patientNumber", nameAndNumber.getNumber());
		hashMap.put("patientName", nameAndNumber.getName());
		hashMap.put("grossAmount", this.formatAmount(payment.getGrossTotal()));
		hashMap.put("depositAmount", this.formatAmount(payment.getDepositAllocatedTotal()));
		hashMap.put("waivedAmount", this.formatAmount(payment.getWaivedTotal()));
		hashMap.put("discountAmount", this.formatAmount(payment.getDiscountTotal()));
		hashMap.put("amountPaid", this.formatAmount(payment.getNetTotal()));
		hashMap.put("paymentType", payment.getPaymentMethod().getName());
		return hashMap;
	}

	private PNameDto getPaymentPatientNumber(PatientBill patientBill) {
		String patientNumber = "";
		String patientName = "";
		if (patientBill.getBillPatientType().equals(BillPatientTypeEnum.REGISTERED.label)) {
			patientNumber = patientBill.getPatient().getPatientNumber();
			patientName = patientBill.getPatient().getFullName();
		} else if (patientBill.getBillPatientType().equals(BillPatientTypeEnum.WALK_IN.label)) {
			patientNumber = "WK/IN"; // walkIn patient does not have patient number
			patientName = patientBill.getWalkInPatient().getFullName();
		}
		return new PNameDto(patientNumber, patientName);
	}

	private InputStream getFilePath(ReceiptTypeEnum typeEnum) {
		if (this.printerSize.isEmpty()) {
			this.printerSize = "58mm";
		}
		switch (typeEnum) {
			case SERVICE_BILL_RECEIPT:
				return ReceiptTypeEnum.SERVICE_BILL_RECEIPT.absoluteFilePath(this.utilService, this.printerSize);
			case DEPOSIT_RECEIPT:
				return ReceiptTypeEnum.DEPOSIT_RECEIPT.absoluteFilePath(this.utilService, printerSize);
			case DRUG_BILL_INVOICE:
				return ReceiptTypeEnum.DRUG_BILL_INVOICE.absoluteFilePath(this.utilService, printerSize);
			case SERVICE_BILL_INVOICE:
				return ReceiptTypeEnum.SERVICE_BILL_INVOICE.absoluteFilePath(this.utilService, printerSize);
			default:
				return null;
		}
	}

	private List<ProductServiceInvoiceItems> getBillItemsFromBill(PatientBill patientBill) {
		if (patientBill.getBillTypeEnum() == PaymentTypeForEnum.SERVICE) {
			return this.billItemService.getItemsFromBill(patientBill);
		} else if (patientBill.getBillTypeEnum() == PaymentTypeForEnum.DRUG) {
			return this.pharmacyBillItemService.getItemsFromBill(patientBill);
		} else {
			return new ArrayList<>();
		}
	}

	// get list of items in the receipt (use type to check if it's a service bill or
	// pharmacy bill)
	private List<ProductServiceInvoiceItems> getBillItemsFromReceipt(
			PatientBill patientBill,
			PaymentTypeForEnum type) {
		List<ProductServiceInvoiceItems> items = new ArrayList<>();
		if (type == PaymentTypeForEnum.SERVICE) {
			items = this.billItemService.getItemsFromBill(patientBill);
		} else if (type == PaymentTypeForEnum.DRUG) {
			// get items from drug bill
			items = this.pharmacyBillItemService.getItemsFromBill(patientBill);
		}
		return items;
	}

	private HashMap<String, Object> getBillInvoiceMap(PatientBill patientBill) {
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("hmisServiceList", new JRBeanCollectionDataSource(this.getBillItemsFromBill(patientBill)));
		hashMap.put("hmisHospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		// set patient data
		// REGISTERED PATIENT BILL
		if (patientBill.getBillPatientType().equals(BillPatientTypeEnum.REGISTERED.label)) {
			hashMap.put("hmisPatientName", patientBill.getPatient().getFullName());
			hashMap.put("hmisPatientNumber", patientBill.getPatient().getPatientNumber());
			if (patientBill.getPatient().getPatientCategory().equals(PatientCategoryEnum.SCHEME)) {
				String insuranceName = patientBill.getPatient().getPatientInsuranceDetail().getScheme()
						.getInsuranceName();
				hashMap.put("patientHmo", insuranceName);
			}
		}
		// WALK-IN PATIENT BILL
		else if (patientBill.getBillPatientType().equals(BillPatientTypeEnum.WALK_IN.label)) {
			hashMap.put("hmisPatientName", patientBill.getWalkInPatient().getFullName());
			hashMap.put("hmisPatientNumber", "WK/IN");
		}
		// ANYTHING ELSE
		else {
			hashMap.put("hmisPatientName", "N/A");
			hashMap.put("hmisPatientNumber", "N/A");
		}
		// invoice data
		hashMap.put("hmisInvoiceDate", patientBill.getCostDate().toString());
		hashMap.put("hmisInvoiceNumber", patientBill.getInvoiceNumber());
		hashMap.put("hmisBilledBy", patientBill.getCostBy().getFullName());
		hashMap.put("hmisIsCreditBill", patientBill.getIsCredit() ? "Yes" : "No");
		hashMap.put("hmisInvoiceGrossTotal", this.formatAmount(patientBill.getGrossTotal()));
		hashMap.put("hmisInvoiceDiscountTotal", this.formatAmount(patientBill.getDiscountTotal()));
		hashMap.put("hmisInvoiceNetTotal", this.formatAmount(patientBill.getNetTotal()));
		hashMap.put("hmisInvoiceOutlet", patientBill.getLocation().getName());
		hashMap.put("logo", this.commonService.getLogoAsStream());

		return hashMap;
	}

	private HashMap<String, Object> getDepositReceiptMap(PaymentReceipt receipt) {
		HashMap<String, Object> map = new HashMap<>();
		PatientDepositLog deposit = receipt.getPayment().getDepositLog();
		map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		map.put("patientName", receipt.getPatientDetail().getFullName());
		map.put("patientNumber", receipt.getPatientDetail().getPatientNumber());
		map.put("receiptNumber", receipt.getReceiptNumber());
		map.put("receiptDate", receipt.getLocalDate().toString());
		map.put("receiptTime", receipt.getLocalTime().withNano(0).toString());
		map.put("receiptDesc", receipt.getPayment().getDepositLog().getDescription());
		map.put("receiptAmount", this.formatAmount(deposit.getDepositAmount()));
		map.put("cashPoint", deposit.getDepartment().getName());
		map.put("shiftNumber", receipt.getShiftNumber());
		map.put("cashier", deposit.getUser().getFullName());
		map.put("logo", this.commonService.getLogoAsStream());
		map.put("paymentMethod", receipt.getPayment().getPaymentMethod().getName());
		return map;
	}

	private String formatAmount(Double amount) {
		return String.format("%,.2f", amount);
	}

}
