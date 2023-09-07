package com.hmis.server.hmis.modules.reports.service;

import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionStatusEnum;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientSearchService;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.reports.dto.*;
import com.hmis.server.hmis.modules.reports.iservice.IEmrReportService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;
import static com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum.DRUG;
import static com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum.SERVICE;

@Service
@Slf4j
public class EmrReportService implements IEmrReportService {
	private final HmisUtilService utilService;
	private final CommonService commonService;
	private final GlobalSettingsImpl globalSettingsService;
	private final PatientSearchService patientSearchService;
	private final PatientAdmissionServiceImpl admissionService;
	private final PatientDetailServiceImpl patientDetailService;

	@Autowired
	public EmrReportService(
			HmisUtilService utilService,
			CommonService commonService,
			GlobalSettingsImpl globalSettingsService,
			PatientSearchService patientSearchService,
			@Lazy PatientAdmissionServiceImpl admissionService,
			@Lazy PatientDetailServiceImpl patientDetailService) {
		this.utilService = utilService;
		this.commonService = commonService;
		this.globalSettingsService = globalSettingsService;
		this.patientSearchService = patientSearchService;
		this.admissionService = admissionService;
		this.patientDetailService = patientDetailService;
	}

	@Override
	public PageResultDto<PatientDetailDto> searchPatientRegister(PatientRegisterDto dto) {
		PageResultDto<PatientDetailDto> result;
		try {
			this.isValid(dto);
			Pageable pageable = this.utilService.transformToPage(dto.getPage());
			LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
			LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());

			Page<PatientDetail> detailPage = this.patientSearchService.searchPatientsWithFilterPageable(dto,
					startDate,
					endDate,
					pageable);
			result = this.mapToPageResultDto(detailPage, dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		return result;
	}

	public byte[] generatePatientRegisterReportByte(PatientRegisterDto dto) {
		byte[] invoice = null;
		try {
			InputStream filePath = this.getEmrReportFilePath();
			HashMap<String, Object> map = this.getPatientRegisterReportMap(dto);
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JREmptyDataSource());
			invoice = JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (JRException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return invoice;
	}

	public List<SearchAdmissionDto> searchPatientAdmissionSession(PatientInterimSearchDto dto) {
		try {
			List<SearchAdmissionDto> result = new ArrayList<>();
			if (dto.getVisitType().equals(AdmissionSessionTypeEnum.CURRENT)) {
				SearchAdmissionDto admissionDto = this.admissionService.findPatientCurrentAdmissionSession(
						dto.getPatient().getPatientId());
				result.add(admissionDto);
			} else if (dto.getVisitType().equals(AdmissionSessionTypeEnum.PREVIOUS)) {
				PatientDetail patient = this.patientDetailService.findPatientDetailById(
						dto.getPatient().getPatientId());
				LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
				LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
				result = this.admissionService.searchAdmissionSessionByDateRange(startDate, endDate, patient);
			}
			return result;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public ResponseEntity<byte[]> prepInterimInvoiceMapData(PatientInterimSearchDto dto) {
		this.validatePatientInterimSearch(dto);
		try {
			if (dto.getVisitType().equals(AdmissionSessionTypeEnum.CURRENT)) {
				PatientAdmission admission = this.admissionService.findPatientCurrentAdmission(
						dto.getPatient().getPatientId());
				return this.generatePatientInterimInvoice(admission);
			} else {
				if (dto.getAdmissionCode() != null) {
					PatientAdmission admission = this.admissionService.findByCode(dto.getAdmissionCode());
					return this.generatePatientInterimInvoice(admission);
				}
				LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
				LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
				PatientDetail patient = this.patientDetailService.findPatientDetailById(
						dto.getPatient().getPatientId());
				List<PatientAdmission> admissionList = this.admissionService.findPatientAdmissionByDateRange(startDate,
						endDate,
						patient);
			}
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public ResponseEntity<byte[]> generatePatientInterimInvoice(PatientAdmission admission) {
		try {
			// String filePath =
			// HmisReportFileEnum.EMR_PATIENT_INTERIM_INVOICE.absoluteFilePath(this.utilService);
			InputStream filePath = HmisReportFileEnum.EMR_PATIENT_INTERIM_INVOICE.absoluteFilePath(this.utilService);
			HashMap<String, Object> map = this.getPatientInterimInvoiceReportData(admission);
			// JasperReport jasperReport = JasperCompileManager.compileReport(new
			// FileInputStream(filePath));
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JREmptyDataSource());
			byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
			return ResponseEntity.status(HttpStatus.OK).body(bytes);
		} catch (JRException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private HashMap<String, Object> getPatientInterimInvoiceReportData(PatientAdmission admission) {
		boolean isDischarged = admission.getAdmissionStatus().equals(PatientAdmissionStatusEnum.DISCHARGED);
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("billsTableDataSet",
				new JRBeanCollectionDataSource(this.getPatientInterimBillsTableData(admission)));
		hashMap.put("receiptTableDataSet",
				new JRBeanCollectionDataSource(this.getPatientInterimReceiptTableData(admission)));
		hashMap.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		hashMap.put("logo", this.commonService.getLogoAsStream());
		hashMap.put("reportHeader", "PATIENT INTERIM INVOICE");
		hashMap.put("patientName", admission.getPatient().getFullName());
		hashMap.put("patientNumber", admission.getPatient().getPatientNumber());
		hashMap.put("patientVisitId", admission.getCode());
		if (admission.getPatient().getPatientContactDetail().getResidentialAddress() == null) {
			hashMap.put("patientAddress", "");
		} else {
			hashMap.put("patientAddress", admission.getPatient().getPatientContactDetail().getResidentialAddress());
		}

		hashMap.put("patientScheme", admission.getPatient().getPatientCategory().name());
		hashMap.put("patientWard", admission.getWard().getDepartment().getName());
		hashMap.put("sessionDate", admission.getAdmittedDate().toString());
		hashMap.put("dischargedDate", isDischarged ? admission.getDischargedDate().toString() : "");
		hashMap.put("numberOfDays", this.admissionService.calculatePatientDaysOnAdmission(admission));
		hashMap.put("diagnosis", isDischarged ? admission.getFinalDiagnosis() : "");
		this.prepPatientInterimInvoiceReportData(admission, hashMap);
		return hashMap;
	}

	private void prepPatientInterimInvoiceReportData(PatientAdmission admission, HashMap<String, Object> hashMap) {
		double totalDiscountAmount = 0;
		double totalWaivedAmount = 0;
		double totalReceiptAmount = 0;

		double totalBillAmount = 0;
		List<PatientPayment> payments = admission.getPayments();
		List<PatientBill> bills = admission.getBills();
		if (!payments.isEmpty()) {
			totalDiscountAmount = payments.stream().mapToDouble(PatientPayment::getDiscountTotal).sum();
			totalWaivedAmount = payments.stream().mapToDouble(PatientPayment::getWaivedTotal).sum();
			totalReceiptAmount = payments.stream().mapToDouble(PatientPayment::getNetTotal).sum();
		}
		if (!bills.isEmpty()) {
			totalBillAmount = bills.stream().mapToDouble(PatientBill::getNetTotal).sum();
		}
		double totalRefundAmount = totalReceiptAmount - totalBillAmount;
		hashMap.put("totalDiscountAmount", totalDiscountAmount);
		hashMap.put("totalWaivedAmount", totalWaivedAmount);
		hashMap.put("totalReceiptAmount", totalReceiptAmount);
		hashMap.put("totalRefundAmount", totalRefundAmount);
	}

	List<PatientInterimReceiptTableDto> getPatientInterimReceiptTableData(PatientAdmission admission) {
		List<PatientInterimReceiptTableDto> list = new ArrayList<>();
		if (!admission.getPayments().isEmpty()) {
			List<PatientPayment> payments = admission.getPayments();
			for (PatientPayment payment : payments) {
				PatientInterimReceiptTableDto dto = new PatientInterimReceiptTableDto();
				dto.setDate(payment.getDate().toString());
				dto.setAmount(payment.getNetTotal());
				dto.setDescription(payment.getReceiptNumber());
				list.add(dto);
			}
		}
		return list;
	}

	List<PatientInterimBillsTableDto> getPatientInterimBillsTableData(PatientAdmission admission) {
		List<PatientInterimBillsTableDto> billList = new ArrayList<>();
		if (!admission.getBills().isEmpty()) {
			for (PatientBill bill : admission.getBills()) {
				PaymentTypeForEnum type = bill.getBillTypeEnum();
				if (type.equals(DRUG)) {
					List<PharmacyBillItem> pharmacyBillItems = bill.getPharmacyBillItems();
					if (pharmacyBillItems != null && !pharmacyBillItems.isEmpty()) {
						for (PharmacyBillItem pharmacyBillItem : pharmacyBillItems) {
							PatientInterimBillsTableDto dto = new PatientInterimBillsTableDto();
							dto.setDate(bill.getCostDate().toLocalDate().toString());
							dto.setDescription(pharmacyBillItem.getDrugRegister().fullBrandName());
							dto.setQuantity(pharmacyBillItem.getQuantity());
							dto.setDiscount(pharmacyBillItem.getDiscountAmount());
							dto.setInvoiceNumber(bill.getInvoiceNumber());
							dto.setAmount(pharmacyBillItem.getNetAmount());
							dto.setRunningAmount(0.00);
							dto.setReceiptNumber(bill.getIsPaid() ? bill.getReceiptNumber() : "");
							dto.setBilledBy(bill.getCostBy().getFullName());
							billList.add(dto);
						}
					}
				} else if (type.equals(SERVICE)) {
					List<PatientServiceBillItem> serviceBillItems = bill.getPatientServiceBillItems();
					if (!serviceBillItems.isEmpty()) {
						for (PatientServiceBillItem serviceBillItem : serviceBillItems) {
							PatientInterimBillsTableDto dto = new PatientInterimBillsTableDto();
							dto.setDate(bill.getCostDate().toLocalDate().toString());
							dto.setDescription(serviceBillItem.getProductService().getName());
							dto.setQuantity(serviceBillItem.getQuantity().intValue());
							dto.setDiscount(
									serviceBillItem.getDiscountAmount() == null ? 0.00
											: serviceBillItem.getDiscountAmount());
							dto.setInvoiceNumber(bill.getInvoiceNumber());
							dto.setAmount(serviceBillItem.getNetAmount());
							dto.setRunningAmount(0.00);
							dto.setReceiptNumber(bill.getIsPaid() ? bill.getReceiptNumber() : "");
							dto.setBilledBy(bill.getCostBy().getFullName());
							billList.add(dto);
						}

					}
				}
			}
		}
		return billList;
	}

	private HashMap<String, Object> getPatientRegisterReportMap(PatientRegisterDto dto) {
		String start = this.utilService.transformToLocalDate(dto.getStartDate()).toString();
		String end = this.utilService.transformToLocalDate(dto.getEndDate()).toString();
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("reportDataList", new JRBeanCollectionDataSource(this.getPatientReportDataList(dto)));
		hashMap.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		hashMap.put("logo", this.commonService.getLogoAsStream());
		hashMap.put("reportHeader", String.format("Registered Patient Report Between %s To %s", start, end));
		return hashMap;
	}

	private List<PatientRegisterReportColDto> getPatientReportDataList(PatientRegisterDto dto) {
		LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
		LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
		List<PatientDetail> patientDetailList = this.patientSearchService.searchPatientsWithFilterAll(dto, startDate,
				endDate);
		List<PatientRegisterReportColDto> list = new ArrayList<>();
		if (!patientDetailList.isEmpty()) {
			// int index = 1;
			for (PatientDetail patientDetail : patientDetailList) {
				PatientRegisterReportColDto col = new PatientRegisterReportColDto();
				// col.setSerialNumber( String.valueOf( index++ ) );
				col.setSerialNumber(null);
				col.setPatientName(patientDetail.getFullName());
				col.setPatientNumber(patientDetail.getPatientNumber());
				col.setPatientDob(patientDetail.getDateOfBirth().toString());
				col.setPatientAge(patientDetail.getAge());
				col.setPatientGender(patientDetail.getGender().getName());
				col.setPatientCategory(patientDetail.getPatientCategory().name());
				col.setPatientAddress(patientDetail.getPatientContactDetail().getResidentialAddress());
				col.setPatientRegisterDate(patientDetail.getRegisterDate().toString());
				col.setPatientFolderNumber(patientDetail.getFolderNumber());
				col.setPatientCardNumber(patientDetail.getCardNumber());
				list.add(col);
			}
		}
		return list;
	}

	private PageResultDto<PatientDetailDto> mapToPageResultDto(Page<PatientDetail> register, PatientRegisterDto dto) {
		List<PatientDetailDto> dtoList = new ArrayList<>();
		List<PatientDetail> content = register.getContent();
		if (!content.isEmpty()) {
			for (PatientDetail patient : content) {
				PatientDetailDto patientDetailDto = patient.transformToDto();
				dtoList.add(patientDetailDto);
			}
		}

		PageDto page = this.utilService.transformToPageDto(register);
		return new PageResultDto<>(page, dtoList);
	}

	private void isValid(PatientRegisterDto dto) {

		if (dto.getStartDate() == null || dto.getEndDate() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start/End Date is Required");
		}
	}

	private void validatePatientInterimSearch(PatientInterimSearchDto dto) {
		if (dto.getVisitType() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit Type Is Required");
		}
		if (dto.getPatient() == null || dto.getPatient().getPatientId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient Is Required");
		}
	}

	private InputStream getEmrReportFilePath() {
		return HmisReportFileEnum.EMR_PATIENT_REGISTER_REPORT.absoluteFilePath(this.utilService);
	}
}