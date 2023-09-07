package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.emr.IService.IPatientRevisitService;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.dto.PatientRevisitDto;
import com.hmis.server.hmis.modules.emr.dto.PatientRevisitTypeEnum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientVisit;
import com.hmis.server.hmis.modules.emr.model.PatientVisitHistory;
import com.hmis.server.hmis.modules.emr.repository.PatientActiveVisitRepository;
import com.hmis.server.hmis.modules.emr.repository.PatientRevisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientRevisitServiceImpl implements IPatientRevisitService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final PatientRevisitRepository revisitRepository;
	private final PatientActiveVisitRepository activeVisitRepository;
	private final PatientDetailServiceImpl patientDetailService;
	private final DepartmentServiceImpl departmentService;
	private final UserServiceImpl userService;
	private final HmisUtilService utilService;
	private final CommonService commonService;
	private final PatientAdmissionServiceImpl admissionService;

	@Autowired
	public PatientRevisitServiceImpl(
			PatientRevisitRepository revisitRepository,
			PatientActiveVisitRepository activeVisitRepository,
			PatientDetailServiceImpl patientDetailService,
			DepartmentServiceImpl departmentService,
			UserServiceImpl userService,
			HmisUtilService utilService,
			CommonService commonService,
			@Lazy
			PatientAdmissionServiceImpl admissionService
	) {
		this.revisitRepository = revisitRepository;
		this.activeVisitRepository = activeVisitRepository;
		this.patientDetailService = patientDetailService;
		this.departmentService = departmentService;
		this.userService = userService;
		this.utilService = utilService;
		this.commonService = commonService;
		this.admissionService = admissionService;
	}

	/**
	 * Don't use this!
	 * This is using the revisit status column on patient model as revisit status.
	 * Should check active visit (hmis_patient_active_visit_data) record for revisit status
	 * should be removed in future!
	 *
	 * @deprecated use {@link #isPatientVisitActive} instead.
	 */
	@Deprecated
	@Override
	public boolean getPatientRevisitStatus(Long patientId) {
		return false;
	}

	@Override
	public boolean isPatientVisitActive(Long patientId) {
		Optional< PatientVisit > optional = this.activeVisitRepository.findByPatient_Id(patientId);
		return optional.isPresent();
	}

	@Override
	public PatientRevisitDto revisitAPatient(PatientRevisitDto dto) {
		if( dto.getPatientDetail() == null || dto.getPatientDetail().getPatientId() == null ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide Patient Before Revisit");
		}
		boolean patientOnAdmission = this.admissionService.isPatientOnAdmission(dto.getPatientDetail().getPatientId());
		if( patientOnAdmission ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient Is Currently On Admission");
		}
		else if( this.isPatientVisitActive(dto.getPatientDetail().getPatientId()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient Is Currently Active");
		}
		dto.setRevisitType(PatientRevisitTypeEnum.MANUAL.name());
		return this.createRevisitRecord(dto);
	}

	@Override
	public List< PatientVisitHistory > findAllVisit() {
		return null;
	}

	@Override
	public List< PatientVisitHistory > findAllVisitWithinDate(LocalDate startDate, LocalDate endDate) {
		return null;
	}

	@Override
	@Transactional
	public void runPatientRevisitStatusScheduler() {
		this.clearAllPatientActiveVisitRecord();
	}

	@Override
	public PatientRevisitDto createRevisitRecord(PatientRevisitDto revisitDto) {
		try {
			revisitDto.setCode(this.generateRevisitCode());
			PatientVisitHistory revisitHistory = new PatientVisitHistory();
			this.setRevisitHistoryModel(revisitDto, revisitHistory);
			PatientVisitHistory patientVisitHistory = this.createRevisitHistory(revisitHistory);
			revisitDto.setId(patientVisitHistory.getId());
			//set patient active visit record
			this.setPatientActiveVisitRecord(revisitDto.getPatientDetail().getPatientId(), patientVisitHistory.getId());
			return revisitDto;
		}
		catch( Exception e ) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public PatientVisitHistory createRevisitHistory(PatientVisitHistory model) {
		return this.revisitRepository.save(model);
	}

	@Override
	public boolean prepPatientAutoRevisit(PatientDetail patient, Long clinicId) {
		try {
			PatientVisitHistory revisitHistory = this.setAutoRevisitHistory(patient, clinicId, PatientRevisitTypeEnum.AUTO);
			this.setPatientActiveVisitRecord(patient.getId(), revisitHistory.getId());
			return true;
		}
		catch( Exception e ) {
			this.LOGGER.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public PatientRevisitDto getPatientActiveVisitRecord(Long patientId) {
		PatientRevisitDto revisitDto = new PatientRevisitDto();
		PatientDetail patientDetail = this.patientDetailService.findPatientDetailById(patientId);
		Optional< PatientVisit > activeVisit = this.activeVisitRepository.findByPatient(patientDetail);
		activeVisit.ifPresent(patientVisit -> this.setRevisitDto(revisitDto, patientVisit.getRevisitHistory()));
		return revisitDto;
	}

	@Override
	public void setPatientVisitRecordAfterRegistration(Long patientId, Long outletId) {
		PatientDetail patient = this.patientDetailService.findPatientDetailById(patientId);
		PatientVisitHistory visitHistory = this.setAutoRevisitHistory(patient, outletId, PatientRevisitTypeEnum.AFTER_REGISTER);
		this.setPatientActiveVisitRecord(patientId, visitHistory.getId());
	}

	private PatientVisitHistory setAutoRevisitHistory(PatientDetail patient, Long clinicId, PatientRevisitTypeEnum revisitType) {
		PatientVisitHistory history = new PatientVisitHistory();
		history.setPatient(patient);
		history.setClinic(this.departmentService.findOneRaw(Optional.ofNullable(clinicId)));
		history.setCode(this.generateRevisitCode());
		history.setRevisitType(revisitType);
		return this.revisitRepository.save(history);
	}

	private void setPatientActiveVisitRecord(Long patientId, Long revisitHistoryId) {
		PatientVisit visit = new PatientVisit();
		visit.setPatient(new PatientDetail(patientId));
		visit.setRevisitHistory(new PatientVisitHistory(revisitHistoryId));
		this.activeVisitRepository.save(visit);
	}

	private void clearAllPatientActiveVisitRecord() {
		this.LOGGER.info("Clearing All Previous Day Patient Active Visit Record");
		this.activeVisitRepository.deleteAllInBatch();
	}

	private void setRevisitHistoryModel(PatientRevisitDto dto, PatientVisitHistory model) {
		if( dto.getId() != null ) {
			model.setId(dto.getId());
		}

		if( dto.getPatientDetail() != null && dto.getPatientDetail().getPatientId() != null ) {
			model.setPatient(this.patientDetailService.findPatientDetailById(dto.getPatientDetail().getPatientId()));
		}

		if( dto.getClinic() != null && dto.getClinic().getId().isPresent() ) {
			model.setClinic(this.departmentService.findOneRaw(Optional.of(dto.getClinic().getId().get())));
		}

		if( dto.getRevisitBy() != null && dto.getRevisitBy().getId().isPresent() ) {
			model.setRevisitBy(this.userService.findOneRaw(dto.getRevisitBy().getId().get()));
		}

		if( dto.getRevisitDate() != null ) {
			LocalDateTime localDateTime = this.utilService.transformToLocalDateTime(dto.getRevisitDate());
			model.setRevisitDate(localDateTime);
		}

		if( dto.getRevisitFrom() != null && dto.getRevisitFrom().getId().isPresent() ) {
			model.setRevisitFrom(this.departmentService.findOneRaw(Optional.of(dto.getRevisitFrom().getId().get())));
		}

		if( dto.getRevisitType() != null ) {
			model.setRevisitType(PatientRevisitTypeEnum.valueOf(dto.getRevisitType()));
		}

		if( dto.getCode() != null ) {
			model.setCode(dto.getCode());
		}

	}

	private String generateRevisitCode() {
		GenerateCodeDto codeDto = new GenerateCodeDto();
		codeDto.setDefaultPrefix(HmisCodeDefaults.REVISIT_CODE_PREFIX_DEFAULT);
		codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.REVISIT_CODE_PREFIX));
		codeDto.setLastGeneratedCode(this.revisitRepository.findTopByOrderByIdDesc().map(PatientVisitHistory::getCode));
		return commonService.generateDataCode(codeDto);
	}

	private void setRevisitDto(PatientRevisitDto dto, PatientVisitHistory model) {
		if( model.getId() != null ) {
			dto.setId(dto.getId());
		}
		if( model.getPatient() != null && model.getPatient().getId() != null ) {
			dto.setPatientDetail(new PatientDetailDto(model.getId()));
		}
		if( model.getClinic() != null && model.getClinic().getId() != null ) {
			dto.setClinic(new DepartmentDto(model.getClinic().getId(), model.getClinic().getName()));
		}
		if( model.getRevisitBy() != null && model.getRevisitBy().getId() != null ) {
			dto.setRevisitBy(new UserDto(Optional.ofNullable(model.getRevisitBy().getId()), Optional.of(model.getRevisitBy().getUserName())));
		}
		if( model.getRevisitDate() != null ) {
			DateDto dateDto = this.utilService.transformToDateDto(model.getRevisitDate());
			dto.setRevisitDate(dateDto);
		}
		if( model.getRevisitFrom() != null && model.getRevisitFrom().getId() != null ) {
			dto.setRevisitFrom(new DepartmentDto(model.getRevisitFrom().getId(), model.getRevisitFrom().getName()));
		}
		if( model.getRevisitType() != null ) {
			dto.setRevisitType(model.getRevisitType().name());
		}
		if( model.getCode() != null ) {
			dto.setCode(model.getCode());
		}
	}
}
