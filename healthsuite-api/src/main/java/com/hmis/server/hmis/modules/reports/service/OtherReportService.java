package com.hmis.server.hmis.modules.reports.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.service.*;
import com.hmis.server.hmis.modules.billing.service.SchemeBillServiceImpl;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.reports.dto.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import static com.hmis.server.hmis.common.constant.HmisConstant.*;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;
import static com.hmis.server.hmis.modules.reports.dto.SchemeConsumptionReportTypeEnum.SUMMARY;

@Service
@Slf4j
public class OtherReportService {

	private final ProductServiceImpl productService;
	private final RevenueDepartmentServiceImpl revenueDepartmentService;
	private final DepartmentServiceImpl departmentService;
	private final HmisUtilService utilService;
	private final GlobalSettingsImpl globalSettingsService;
	private final CommonService commonService;
	private final SchemeBillServiceImpl schemeBillService;
	private final SchemeServiceImpl schemeService;
	private final PatientDetailServiceImpl patientDetailService;

	@Autowired
	public OtherReportService(
			@Lazy ProductServiceImpl productService,
			@Lazy RevenueDepartmentServiceImpl revenueDepartmentService,
			@Lazy DepartmentServiceImpl departmentService,
			@Lazy HmisUtilService utilService,
			@Lazy GlobalSettingsImpl globalSettingsService,
			@Lazy CommonService commonService,
			@Lazy SchemeBillServiceImpl schemeBillService,
			@Lazy SchemeServiceImpl schemeService,
			@Lazy PatientDetailServiceImpl patientDetailService) {
		this.productService = productService;
		this.revenueDepartmentService = revenueDepartmentService;
		this.departmentService = departmentService;
		this.utilService = utilService;
		this.globalSettingsService = globalSettingsService;
		this.commonService = commonService;
		this.schemeBillService = schemeBillService;
		this.schemeService = schemeService;
		this.patientDetailService = patientDetailService;
	}

	public ResponseEntity<byte[]> downloadServiceChargeSheetReport(
			ServiceChargeCategoryEnum categoryEnum, String searchById) {
		boolean isSearchByServiceDepartment = categoryEnum.equals(ServiceChargeCategoryEnum.SERVICE_DEPARTMENT);
		boolean isSearchByRevenueDepartment = categoryEnum.equals(ServiceChargeCategoryEnum.REVENUE_DEPARTMENT);

		if (!categoryEnum.equals(ServiceChargeCategoryEnum.BOTH) && searchById == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search ID is required");
		}
		List<ServiceChargeGroupedByRevenue> resultSet = new ArrayList<>();

		if (isSearchByRevenueDepartment) {
			// search by revenue department
			RevenueDepartment revenueDepartment = this.revenueDepartmentService.findOneRaw(
					Long.valueOf(searchById));
			ServiceChargeGroupedByRevenue groupedByRevenue = this.productService.getServiceChargeGroupedByRevenue(
					revenueDepartment);
			resultSet.add(groupedByRevenue);
			return ResponseEntity.ok().body(getServiceChargeSheet(resultSet));
		} else if (isSearchByServiceDepartment) {
			// search by service department
			Department department = this.departmentService.findOne(Long.valueOf(searchById));
			ServiceChargeGroupedByService groupedByService = this.productService.getServiceChargeGroupedByService(
					department);
			ServiceChargeGroupedByRevenue groupedByRevenue = new ServiceChargeGroupedByRevenue();
			groupedByRevenue.setRevenueDepartmentName(null);
			groupedByRevenue.setGroupByService(new JRBeanCollectionDataSource(Arrays.asList(groupedByService)));
			resultSet.add(groupedByRevenue);
			return ResponseEntity.ok().body(getServiceChargeSheet(resultSet));
		} else {
			resultSet = this.productService.getServiceChargeGroupedByBoth();
			return ResponseEntity.ok().body(getServiceChargeSheet(resultSet));
		}
	}

	/**
	 * <p>
	 * when called will generate a pdf file as byte[]
	 * containing scheme consumption data.
	 * </p>
	 * <p>
	 * dto contains search parameters used to query data from storage.
	 * </p>
	 *
	 * @param dto SchemeConsumptionReportDto
	 * @return ResponseEntity
	 */
	public ResponseEntity<byte[]> downloadSchemeConsumptionReport(SchemeConsumptionReportDto dto) {
		try {
			SchemeConsumptionReportTypeEnum reportType = dto.getReportType();
			if (reportType == SUMMARY) {
				return ResponseEntity.ok().body(this.getSchemeConsumptionSummaryReport(dto));
			}
			return ResponseEntity.ok().body(this.getSchemeConsumptionDetailedReport(dto));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	private Map<String, Object> getSchemeConsumptionSummaryMap(String reportTitle, String hmoTitle) {
		Map<String, Object> map = new HashMap<>();
		map.put("reportTitle", reportTitle != null ? reportTitle : NIL);
		map.put("hmoTitle", hmoTitle != null ? hmoTitle : NIL);
		map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		map.put("logo", this.commonService.getLogoAsStream());
		return map;
	}

	private byte[] getSchemeConsumptionSummaryReport(SchemeConsumptionReportDto dto) {
		try {
			LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
			LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
			SchemeConsumptionSearchByEnum searchBy = dto.getSearchBy();

			switch (searchBy) {
				case SERVICE_DEPARTMENT:
					return this.prepareSchemeConsumptionSummaryByServiceDepartment(dto, startDate, endDate);
				case REVENUE_DEPARTMENT:
					return this.prepareSchemeConsumptionSummaryByRevenueDepartment(dto, startDate, endDate);
				case HMO:
					return this.prepareSchemeConsumptionSummaryByHmo(dto, startDate, endDate);
				case PATIENT:
					return this.prepareSchemeConsumptionSummaryByPatient(dto, startDate, endDate);
				default:
					break;
			}

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown Search Parameter");

		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	private byte[] prepareSchemeConsumptionSummaryByServiceDepartment(
			SchemeConsumptionReportDto dto, LocalDate startDate, LocalDate endDate) {
		Department serviceDepartment = this.departmentService.findOne(dto.getServiceDepartment().getId().get());
		String reportTitle = String.format(
				"NHIS SUMMARY CONSUMPTION REPORT FROM %s TO %s \n FILTERED BY SERVICE DEPARTMENT \n [ %s ]",
				startDate.toString(), endDate.toString(), serviceDepartment.getName());
		Map<String, List<SchemeConsumptionPropsDto>> resultMap = this.schemeBillService
				.getSchemeConsumptionReportSummaryByServiceDepartment(
						serviceDepartment, startDate, endDate);
		resultMap.entrySet().removeIf(stringListEntry -> stringListEntry.getValue().isEmpty());
		return this.prepareSchemeConsumptionSummaryGroup(resultMap, reportTitle, HMO);
	}

	private byte[] prepareSchemeConsumptionSummaryByRevenueDepartment(
			SchemeConsumptionReportDto dto, LocalDate startDate, LocalDate endDate) {

		RevenueDepartment revenueDepartment = this.revenueDepartmentService.findOneRaw(
				dto.getRevenueDepartment().getId().get());
		String reportTitle = String.format(
				"NHIS SUMMARY CONSUMPTION REPORT FROM %s TO %s FILTERED BY REVENUE DEPARTMENT \n [ %s ]",
				startDate.toString(), endDate.toString(), revenueDepartment.getName());

		Map<String, List<SchemeConsumptionPropsDto>> resultMap = this.schemeBillService
				.getSchemeConsumptionReportSummaryByRevenueDepartment(
						revenueDepartment, startDate, endDate);
		resultMap.entrySet().removeIf(stringListEntry -> stringListEntry.getValue().isEmpty());
		return this.prepareSchemeConsumptionSummaryGroup(resultMap, reportTitle, HMO);
	}

	private byte[] prepareSchemeConsumptionSummaryGroup(
			Map<String, List<SchemeConsumptionPropsDto>> resultMap, String reportTitle, String groupTitlePrefix) {

		List<SchemeConsumptionByRevenueDto> resultSet = new ArrayList<>();
		if (!resultMap.entrySet().isEmpty()) {
			for (Map.Entry<String, List<SchemeConsumptionPropsDto>> entry : resultMap.entrySet()) {
				SchemeConsumptionByRevenueDto grouped = new SchemeConsumptionByRevenueDto();
				grouped.setGroupTitle(groupTitlePrefix + " " + entry.getKey());
				grouped.setGroupData(new JRBeanCollectionDataSource(entry.getValue()));
				resultSet.add(grouped);
			}
		}
		Map objectMap = this.getSchemeConsumptionSummaryMap(reportTitle, null);
		return this.generateSchemeConsumptionSummaryWithSubReport(resultSet, objectMap);
	}

	private byte[] prepareSchemeConsumptionSummaryByHmo(
			SchemeConsumptionReportDto dto, LocalDate startDate, LocalDate endDate) {
		Scheme scheme = this.schemeService.findById(dto.getSchemeData().getId());
		String hmoReportTitle = String.format(
				"NHIS SUMMARY CONSUMPTION REPORT FROM %s TO %s BY HMO",
				startDate.toString(), endDate.toString());
		String insuranceName = scheme.getInsuranceName() != null ? scheme.getInsuranceName() : NIL;
		String hmoTitle = String.format("HMO NAME: %s", insuranceName);
		List<SchemeConsumptionPropsDto> reportData = this.schemeBillService.getSchemeConsumptionReportSummaryByHmo(
				scheme, startDate, endDate);
		return this.generateSchemeConsumptionSummaryReport(
				reportData, getSchemeConsumptionSummaryMap(hmoReportTitle, hmoTitle));
	}

	private byte[] prepareSchemeConsumptionSummaryByPatient(
			SchemeConsumptionReportDto dto, LocalDate startDate, LocalDate endDate) {
		PatientDetail patient = this.patientDetailService.findPatientDetailById(
				dto.getPatient().getPatientId());

		Optional<String> insuranceName = HmisUtilService.getPatientSchemeName(patient);
		String patientReportTitle = String.format(
				"NHIS SUMMARY CONSUMPTION REPORT FROM %s TO %s BY PATIENT",
				startDate.toString(), endDate.toString());
		String patientTitle = String.format(
				"PATIENT NAME: %s - [%s] \n \n%s", patient.getFullName(),
				patient.getPatientNumber(), insuranceName.map(s -> "HMO NAME: " + s).orElse(""));
		List<SchemeConsumptionPropsDto> reportData = this.schemeBillService.getSchemeConsumptionReportSummaryByPatient(
				patient, startDate, endDate);
		return this.generateSchemeConsumptionSummaryReport(
				reportData, getSchemeConsumptionSummaryMap(patientReportTitle, patientTitle));
	}

	private byte[] generateSchemeConsumptionSummaryWithSubReport(
			List<SchemeConsumptionByRevenueDto> resultSet, Map map) {
		try {
			InputStream mainReport = HmisReportFileEnum.ACC_SCHEME_CONSUMPTION_SUMMARY_MAIN.absoluteFilePath(
					this.utilService);

			JasperReport jasperReport = JasperCompileManager.compileReport(mainReport);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, map,
					resultSet.isEmpty() ? new JREmptyDataSource() : new JRBeanCollectionDataSource(resultSet));
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (JRException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Method: generate scheme consumption summary report pdf
	 * </p>
	 * Description: uses the report data to compile a {@code jrxml } file into bytes
	 * {@code (PDF)}
	 *
	 * @param reportData List
	 * @param map        Map
	 * @return byte[]
	 */
	private byte[] generateSchemeConsumptionSummaryReport(
			List<SchemeConsumptionPropsDto> reportData,
			Map<String, Object> map) {
		try {
			map.put("dataset", new JRBeanCollectionDataSource(reportData));
			InputStream filePath = HmisReportFileEnum.ACC_SCHEME_CONSUMPTION_SUMMARY.absoluteFilePath(this.utilService);
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, map, new JREmptyDataSource());

			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	private byte[] getSchemeConsumptionDetailedReport(
			SchemeConsumptionReportDto dto) {
		LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
		LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
		SchemeConsumptionSearchByEnum searchBy = dto.getSearchBy();

		switch (searchBy) {
			case SERVICE_DEPARTMENT:
				Department department = this.departmentService.findOne(dto.getServiceDepartment().getId().get());
				Map<String, List<SchemeConsumptionPropsDto>> groupedByServiceDepartment = this.schemeBillService
						.getSchemeBillListGroupedByServiceDepartment(
								department, startDate, endDate);
				// user result to create pdf
				break;
			case REVENUE_DEPARTMENT:
				RevenueDepartment revenueDepartment = this.revenueDepartmentService.findOneRaw(
						dto.getRevenueDepartment().getId().get());
				Map<String, List<SchemeConsumptionPropsDto>> groupedByRevenueDepartment = this.schemeBillService
						.getSchemeBillListGroupedByRevenueDepartment(
								revenueDepartment, startDate, endDate);
				// use result set to create pdf report
				break;
			case HMO:
				Scheme scheme = this.schemeService.findById(dto.getSchemeData().getId());
				Map<String, List<SchemeConsumptionPropsDto>> groupedByScheme = this.schemeBillService
						.getSchemeBillListGroupedByScheme(
								scheme, startDate, endDate);

				break;
			case PATIENT:
				System.out.println("patient");
				PatientDetail patient = this.patientDetailService.findPatientDetailById(
						dto.getPatient().getPatientId());
				Map<String, List<SchemeConsumptionPropsDto>> groupedByPatient = this.schemeBillService
						.getSchemeBillListGroupedByPatient(
								patient, startDate, endDate);
				// patient result

				break;
			default:
				break;

		}

		return null;
	}

	private byte[] getServiceChargeSheet(List<ServiceChargeGroupedByRevenue> resultSet) {
		try {
			this.trimServiceChargeSheet(resultSet);
			InputStream filePath = HmisReportFileEnum.SERVICE_CHARGE_LIST.absoluteFilePath(this.utilService);
			HashMap<String, Object> map = new HashMap<>();
			map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
			map.put("logo", this.commonService.getLogoAsStream());
			if (this.globalSettingsService.isNhisServicePriceEnabled()) {
				map.put("footerText", LAB_REPORT_NHIS_PRICE_FOOTER);
			}
			JasperReport subReport = this.commonService.compileReportDesign(utilService,
					HmisReportFileEnum.SERVICE_CHARGE_LIST_SUB);
			map.put("subReportFile", subReport);
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map,
					new JRBeanCollectionDataSource(resultSet));
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (JRException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Method: trimServiceChargeSheet
	 * </p>
	 * <p>
	 * Description: removes any item where groupedByService (type of
	 * JRBeanCollectionDataSource) data property is empty.
	 * </p>
	 *
	 * @param resultSet List
	 */
	private void trimServiceChargeSheet(List<ServiceChargeGroupedByRevenue> resultSet) {
		resultSet.removeIf(e -> {
			return e.getGroupByService().getData().isEmpty();
		});
	}

}
