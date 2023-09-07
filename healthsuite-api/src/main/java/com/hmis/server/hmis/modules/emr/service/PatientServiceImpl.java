package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.NationalityDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.common.service.NationalityServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.modules.billing.dto.PatientBillStatsDto;
import com.hmis.server.hmis.modules.billing.service.BillServiceImpl;
import com.hmis.server.hmis.modules.billing.service.DepositServiceImpl;
import com.hmis.server.hmis.modules.clearking.service.DoctorRequestServiceImpl;
import com.hmis.server.hmis.modules.emr.IService.IPatientService;
import com.hmis.server.hmis.modules.emr.dto.*;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum;
import com.hmis.server.hmis.modules.reports.dto.SearchAdmissionDto;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;

@Service
@Slf4j
public class PatientServiceImpl implements IPatientService {
	private final PatientDetailServiceImpl detailService;
	private final PatientSearchService searchService;
	private final PatientAdmissionServiceImpl admissionService;
	private final PatientRevisitServiceImpl revisitService;
	private final PatientAppointmentSetupServiceImpl appointmentSetupService;
	private final PatientAppointmentServiceImpl appointmentService;
	private final DepositServiceImpl depositService;
	private final DoctorRequestServiceImpl doctorRequestService;
	private final BillServiceImpl billService;
	private final HmisUtilService utilService;
	private final GlobalSettingsImpl globalSettings;
	private final CommonService commonService;
	private final NationalityServiceImpl nationalityService;

	@Autowired
	public PatientServiceImpl(
			PatientDetailServiceImpl detailService,
			PatientSearchService searchService,
			@Lazy PatientAdmissionServiceImpl admissionService,
			@Lazy PatientRevisitServiceImpl revisitService,
			PatientAppointmentSetupServiceImpl appointmentSetupService,
			PatientAppointmentServiceImpl appointmentService,
			@Lazy DepositServiceImpl depositService,
			DoctorRequestServiceImpl doctorRequestService,
			@Lazy BillServiceImpl billService,
			@Lazy HmisUtilService utilService,
			@Lazy GlobalSettingsImpl globalSettings,
			@Lazy CommonService commonService,
			@Lazy NationalityServiceImpl nationalityService) {
		this.detailService = detailService;
		this.searchService = searchService;
		this.admissionService = admissionService;
		this.revisitService = revisitService;
		this.appointmentSetupService = appointmentSetupService;
		this.appointmentService = appointmentService;
		this.depositService = depositService;
		this.doctorRequestService = doctorRequestService;
		this.billService = billService;
		this.utilService = utilService;
		this.globalSettings = globalSettings;
		this.commonService = commonService;
		this.nationalityService = nationalityService;
	}

	@Override
	public PatientDetailDto patientRegistration(PatientDetailDto patientDetailDto) {
		try {
			this.onValidatePayloadBeforePatientRegistration(patientDetailDto);
			return this.detailService.createPatient(patientDetailDto);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	public void updatePatientSchemeDetail(PatientDetailDto patientDetailDto) {
		this.detailService.updatePatientScheme(patientDetailDto);
	}

	@Override
	public ResponseDto patientRecordUpdate(PatientDetailDto patientDetailDto) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setData(this.detailService.updatePatientDetail(patientDetailDto));
		responseDto.setHttpStatusText("Successful");
		return responseDto;
	}

	@Override
	public List<PatientDetailDto> patientRecordTypeaheadSearch(
			String searchParam,
			boolean checkAdmission,
			boolean loadAdmission,
			boolean loadRevisit,
			boolean loadSchemeDiscount,
			boolean loadDeposit,
			boolean loadDrugRequest,
			boolean loadLabRequest,
			boolean loadRadiologyRequest) {
		List<PatientDetailDto> patientDetailDtoList = new ArrayList<>();
		List<PatientDetail> patientDetails = this.searchService.searchPatients(searchParam);
		if (!patientDetails.isEmpty()) {
			for (PatientDetail patientDetail : patientDetails) {
				PatientDetailDto patientDetailDto = patientDetail.transformToDto();
				boolean patientOnAdmission = this.admissionService.isPatientOnAdmission(patientDetail.getId());
				if (checkAdmission) {
					patientDetailDto.setIsOnAdmission(patientOnAdmission);
				}

				if (loadAdmission && patientOnAdmission) {
					PatientAdmissionDto patientAdmission = this.admissionService.findPatientAdmission(
							patientDetail.getId());
					if (patientAdmission.getId() != null) {
						patientDetailDto.setAdmission(patientAdmission);
					}
				}

				if (loadRevisit) {
					PatientRevisitDto record = this.revisitService.getPatientActiveVisitRecord(patientDetail.getId());
					patientDetailDto.setPatientRevisitDto(record);
					patientDetailDto.setRevisitStatus(ObjectUtils.isNotEmpty(record.getCode()));
				}

				if (loadSchemeDiscount && this.isSchemePatient(patientDetail)) {
					int discount = patientDetail.getPatientInsuranceDetail().getScheme().getDiscount();
					patientDetailDto.setPatientSchemeDiscount(discount);
				}

				if (loadDeposit) {
					patientDetailDto.setTotalDepositAmount(
							this.depositService.findPatientDepositAmount(patientDetail));
				}

				if (loadDrugRequest) {
					LocalDate now = LocalDate.now();
					patientDetailDto.setDrugRequestList(
							this.doctorRequestService.getDoctorDrugRequest(patientDetail, now, now));
				}

				if (loadLabRequest) {
					LocalDate now = LocalDate.now();
					patientDetailDto.setLabRequestList(
							this.doctorRequestService.getDoctorLabRequest(patientDetail, now, now));
				}

				if (loadRadiologyRequest) {
					LocalDate now = LocalDate.now();
					patientDetailDto.setRadiologyRequestList(
							this.doctorRequestService.getDoctorRadiologyRequest(patientDetail, now, now));
				}

				patientDetailDtoList.add(patientDetailDto);

			}
		}
		return patientDetailDtoList;
	}

	@Override
	public ResponseDto patientRevisit(PatientRevisitDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			response.setData(this.revisitService.revisitAPatient(dto));
			response.setMessage(HmisConstant.SUCCESS_MESSAGE);
			return response;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@Override
	public ResponseEntity<MessageDto> patientAdmission(PatientAdmissionDto admissionDto) {
		return this.admissionService.admitPatient(admissionDto);
	}

	@Override
	public PatientAppointmentSetupDto createAppointmentSetup(PatientAppointmentSetupDto dto) {
		return this.appointmentSetupService.createAppointmentSetup(dto);
	}

	@Override
	public List<PatientAppointmentDto> findPatientOpenAppointment(Long patientId) {
		return this.appointmentService.findAllPatientOpenAppointment(patientId);
	}

	@Override
	public PatientAppointmentDto createBooking(PatientAppointmentDto dto) {
		return this.appointmentService.createAppointment(dto);
	}

	@Override
	public ResponseDto<Boolean> cancelPatientAppointment(Long appointmentId) {
		boolean cancelPatientAppointment = this.appointmentService.cancelPatientAppointment(appointmentId);
		ResponseDto<Boolean> responseDto = new ResponseDto<>();
		responseDto.setData(cancelPatientAppointment);
		return responseDto;
	}

	@Override
	public String getPatientFullName(PatientDetail patient) {
		return patient.getFullName();
	}

	@Override
	public String getPatientFullName(Long patientId) {
		return this.detailService.findPatientDetailById(patientId).getFullName();
	}

	@Override
	public boolean isSchemePatient(PatientDetail patientDetail) {
		PatientCategoryEnum patientCategory = patientDetail.getPatientCategory();
		return patientCategory == PatientCategoryEnum.SCHEME;
	}

	@Override
	public PatientDetailDto findPatientById(Long patientId) {
		try {
			PatientDetail detail = this.detailService.findPatientDetailById(patientId);
			PatientDetailDto detailDto = detail.transformToDto();
			// add patient revisit payload
			PatientRevisitDto record = this.revisitService.getPatientActiveVisitRecord(detail.getId());
			detailDto.setPatientRevisitDto(record);
			return detailDto;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Patient ID");
		}

	}

	public PatientBillStatsDto getRevisitAccountSummary(long patientId) {
		return this.billService.getPatientAccountSummaryStats(patientId);
	}

	public ResponseEntity<byte[]> dischargePatientAndGenerateGatePass(PatientDischargeDto dto) {
		this.onValidateBeforeDischargePatient(dto);
		PatientAdmission dischargeAdmission = this.admissionService.dischargePatient(dto);
		return this.getPatientGatePass(dto, dischargeAdmission);
	}

	public NationalityDto getNationalityAndLga(Long nationalityId) {
		return this.nationalityService.getParentByChildId(nationalityId);
	}

	public ResponseEntity<?> updatePatientSchemeDetails(PatientDetailDto patient) {
		PatientCategoryEnum patientCategory = patient.getPatientCategoryEnum();
		if (!patientCategory.equals(PatientCategoryEnum.SCHEME)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unaccepted patient category");
		}

		try {
			this.detailService.updatePatientScheme(patient);
			return ResponseEntity.ok().body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	public ResponseEntity<?> changePatientCategory(PatientCategoryUpdate dto) {
		try {
			this.detailService.changePatientCategory(dto);
			return ResponseEntity.ok().body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	private ResponseEntity<byte[]> getPatientGatePass(PatientDischargeDto dto, PatientAdmission dischargeAdmission) {
		try {
			Map<String, Object> param = this.getPatientDischargeGatePassMap(dto, dischargeAdmission);
			InputStream filePath = HmisReportFileEnum.EMR_PATIENT_DISCHARGE_GATE_PASS
					.absoluteFilePath(this.utilService);
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JREmptyDataSource());
			byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
			return ResponseEntity.status(HttpStatus.OK).body(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private Map<String, Object> getPatientDischargeGatePassMap(PatientDischargeDto dto, PatientAdmission admission) {
		PatientDetail patient = admission.getPatient();
		SearchAdmissionDto admissionSession = this.admissionService.mapDischargedAdmissionToSearchDto(admission);
		Map<String, Object> map = new HashMap<>();
		map.put("hospitalName", this.globalSettings.findValueByKey(HOSPITAL_NAME).toUpperCase());
		map.put("logo", this.commonService.getLogoAsStream());
		map.put("reportHeader", "PATIENT GATE PASS");
		map.put("folderNumber", patient.getFolderNumber() != null ? patient.getFolderNumber() : "");
		map.put("patientNumber", patient.getPatientNumber());
		map.put("patientName", patient.getFullName());
		map.put("patientWard", admission.getWard().getDepartment().getName());
		map.put("netBillTotal", admissionSession.getNetAmount());
		map.put("admissionDate", admission.getAdmittedDate().toString());
		map.put("dischargeStatus", admission.getDischargeStatus());
		map.put("patientAge", patient.getAge());
		map.put("patientGender", patient.getGender().getName());
		map.put("dischargeDate", admission.getDischargedDate().toString());
		map.put("dischargeBy", admission.getDischargedBy().getFullName());
		return map;
	}

	private void onValidatePayloadBeforePatientRegistration(PatientDetailDto patientDetailDto) {
		if (patientDetailDto.getPatientCategoryEnum() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					HmisExceptionMessage.EXCEPTION_NO_PATIENT_CATEGORY);
		}

		if (patientDetailDto.getPatientCategoryEnum().equals(
				PatientCategoryEnum.SCHEME) && patientDetailDto.getPatientInsurance() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisExceptionMessage.EXCEPTION_NHIS_RECORD);
		}
	}

	private void onValidateBeforeDischargePatient(PatientDischargeDto dto) {
		if (dto.getPatient() == null || dto.getPatient().getPatientId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisExceptionMessage.PATIENT_IS_REQUIRED);
		}
		if (dto.getDischargedBy() == null || !dto.getDischargedBy().getId().isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discharged By is Required");
		}
		if (dto.getDischargedDate() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discharged Date is Required");
		}

		// todo:: check patient outstanding balance before discharge
	}
}
