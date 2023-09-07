package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.SpecialityUnitServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.dto.GeneralClerkDeskDto;
import com.hmis.server.hmis.modules.clearking.dto.OutPatientDeskDto;
import com.hmis.server.hmis.modules.clearking.iservice.IClerkingGeneralOutPatientDeskService;
import com.hmis.server.hmis.modules.clearking.model.ClerkGeneralClerkDesk;
import com.hmis.server.hmis.modules.clearking.model.ClerkingGeneralOutPatientDesk;
import com.hmis.server.hmis.modules.clearking.repository.ClerkGeneralClerkDeskRepository;
import com.hmis.server.hmis.modules.clearking.repository.ClerkOutPatientDeskRepository;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
public class ClerkingGeneralOutPatientDeskServiceImpl implements IClerkingGeneralOutPatientDeskService {
    private final ClerkOutPatientDeskRepository outPatientDeskRepository;
    private final ClerkGeneralClerkDeskRepository generalClerkDeskRepository;

    private final PatientBackgroundHistoryServiceImpl backgroundHistoryService;
    private final PatientClinicalAssessmentServiceImpl clinicalAssessmentService;
    private final PatientOtherInformationServiceImpl otherInformationService;
    private final SpecialityUnitServiceImpl specialityUnitService;
    private final PatientDetailServiceImpl patientDetailService;
    private final PatientInformantServiceImpl informantService;
    private final PatientPhysicalServiceImpl physicalService;
    private final PatientSystemicServiceImpl systemicService;
    private final PatientCardioVascularServiceImpl cardioVascularService;
    private final PatientAbdomenExaminationServiceImpl abdomenExaminationService;
    private final PatientPerineumServiceImpl perineumService;
    private final PatientMusculoSkeletalServiceImpl musculoSkeletalService;
    private final PatientNeurologicalServiceImpl neurologicalService;
    private final PatientActualDiagnosisServiceImpl actualDiagnosisService;
    private final DepartmentServiceImpl departmentService;
    private final UserServiceImpl userService;
    private final DoctorClerkingTemplateServiceImpl templateService;
    private final DoctorRequestServiceImpl doctorRequestService;
    private final ClerkConsultationServiceImpl clerkConsultationService;


    @Autowired
    public ClerkingGeneralOutPatientDeskServiceImpl(
            ClerkOutPatientDeskRepository outPatientDeskRepository,
            ClerkGeneralClerkDeskRepository generalClerkDeskRepository,
            PatientBackgroundHistoryServiceImpl backgroundHistoryService,
            PatientClinicalAssessmentServiceImpl clinicalAssessmentService,
            PatientOtherInformationServiceImpl otherInformationService,
            SpecialityUnitServiceImpl specialityUnitService,
            PatientDetailServiceImpl patientDetailService,
            PatientInformantServiceImpl informantService,
            PatientPhysicalServiceImpl physicalService,
            PatientSystemicServiceImpl systemicService,
            PatientCardioVascularServiceImpl cardioVascularService,
            PatientAbdomenExaminationServiceImpl abdomenExaminationService,
            PatientPerineumServiceImpl perineumService,
            PatientMusculoSkeletalServiceImpl musculoSkeletalService,
            PatientNeurologicalServiceImpl neurologicalService,
            PatientActualDiagnosisServiceImpl actualDiagnosisService,
            DepartmentServiceImpl departmentService,
            UserServiceImpl userService,
            DoctorClerkingTemplateServiceImpl templateService,
            DoctorRequestServiceImpl doctorRequestService,
            @Lazy ClerkConsultationServiceImpl clerkConsultationService
    ) {
        this.outPatientDeskRepository = outPatientDeskRepository;
        this.generalClerkDeskRepository = generalClerkDeskRepository;
        this.backgroundHistoryService = backgroundHistoryService;
        this.clinicalAssessmentService = clinicalAssessmentService;
        this.otherInformationService = otherInformationService;
        this.specialityUnitService = specialityUnitService;
        this.patientDetailService = patientDetailService;
        this.informantService = informantService;
        this.physicalService = physicalService;
        this.systemicService = systemicService;
        this.cardioVascularService = cardioVascularService;
        this.abdomenExaminationService = abdomenExaminationService;
        this.perineumService = perineumService;
        this.musculoSkeletalService = musculoSkeletalService;
        this.neurologicalService = neurologicalService;
        this.actualDiagnosisService = actualDiagnosisService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.templateService = templateService;
        this.doctorRequestService = doctorRequestService;
        this.clerkConsultationService = clerkConsultationService;
    }

    @Transactional
    public ResponseEntity<MessageDto> saveGeneralClerkingDesk(GeneralClerkDeskDto dto) {
        if (dto.getPatient() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisExceptionMessage.PATIENT_IS_REQUIRED);
        try {
            ClerkGeneralClerkDesk desk = new ClerkGeneralClerkDesk();
            desk.setPatientDetail(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
            desk.setClerkedBy(this.userService.findOneRaw(dto.getClerkedBy().getId().get()));
            desk.setClinic(this.departmentService.findOneRaw(dto.getLocation().getId()));
            if (dto.getConsultant() != null) {
                desk.setConsultant(this.userService.findOneRaw(dto.getConsultant().getId().get()));
            }
            if (dto.getSpecialityUnit() != null) {
                desk.setSpecialityUnit(this.specialityUnitService.findOneRaw(dto.getSpecialityUnit().getId().get()));
            }
            if (dto.getHasInformantDetail() != null && dto.getHasInformantDetail()) {
                desk.setHasInformantDetail(true);
                desk.setInformantDetails(this.informantService.save(dto.getInformantDetails()));
            }
            if (dto.getFollowUpNote() != null) {
                desk.setFollowUpNote(dto.getFollowUpNote());
            }
            if (dto.getProvisionalDiagnosis() != null) {
                desk.setProvisionalDiagnosis(dto.getProvisionalDiagnosis());
            }
            if (dto.getBackgroundHistory() != null) {
                desk.setBackgroundHistory(this.backgroundHistoryService.save(dto.getBackgroundHistory()));
            }
            if (dto.getActualDiagnosis() != null) {
                desk.setActualDiagnosis(this.actualDiagnosisService.save(dto.getActualDiagnosis()));
            }
            if (dto.getIsSaveAsTemplate() != null && dto.getIsSaveAsTemplate()) {
                this.templateService.saveGeneralClerkingDeskTemplate(desk, dto.getSaveAsTemplateName());
            }
            // save doctor request on clerking tabs
            if (ObjectUtils.isNotEmpty(dto.getDoctorRequest())) {
                this.doctorRequestService.saveDoctorRequest(dto.getDoctorRequest());
            }
            ClerkGeneralClerkDesk generalClerkDesk = this.generalClerkDeskRepository.save(desk);
            this.clerkConsultationService.saveGeneralConsultation(generalClerkDesk);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public GeneralClerkDeskDto mapGeneralClerkModelToDto(ClerkGeneralClerkDesk model) {
        GeneralClerkDeskDto dto = new GeneralClerkDeskDto();
        dto.setId(model.getId());
        dto.setPatient(model.getPatientDetail().transformToDto());
        dto.setClerkedBy(this.userService.mapToDtoClean(model.getClerkedBy()));
        dto.setLocation(this.departmentService.mapModelToDto(model.getClinic()));
        dto.setProvisionalDiagnosis(dto.getProvisionalDiagnosis());
        if (ObjectUtils.isNotEmpty(model.getConsultant())) {
            dto.setConsultant(this.userService.mapToDtoClean(model.getConsultant()));
        }
        if (ObjectUtils.isNotEmpty(model.getSpecialityUnit())){
            dto.setSpecialityUnit(this.specialityUnitService.mapModelToDto(model.getSpecialityUnit()));
        }
        dto.setBackgroundHistory(this.backgroundHistoryService.mapToDto(model.getBackgroundHistory()));
        dto.setActualDiagnosis(this.actualDiagnosisService.mapToDto(model.getActualDiagnosis()));
        dto.setInformantDetails(null);
        dto.setFollowUpNote(null);
        return dto;
    }

    @Override
    @Transactional
    public ResponseDto<String> saveOutPatientDeskAndTemplate(OutPatientDeskDto dto) {
        try {
            this.onValidateBeforeSave(dto);
            ClerkingGeneralOutPatientDesk model = this.mapOutPatientDeskDtoToModel(dto);
            ClerkingGeneralOutPatientDesk desk = this.saveSession(model);
            if (dto.getIsSaveAsTemplate()) {
                this.templateService.saveGeneralOutPatientDeskTemplate(desk,
                        dto.getTemplateName());
            }
            // save doctor request on clerking tabs
            if (ObjectUtils.isNotEmpty(dto.getDoctorRequest())) {
                this.doctorRequestService.saveDoctorRequest(dto.getDoctorRequest());
            }
            return new ResponseDto<>(HmisConstant.SUCCESS_MESSAGE);
        } catch (Exception e) {
            // throw runtime exception to enable persistent rollback  (if any error occurred)
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ClerkingGeneralOutPatientDesk saveSession(ClerkingGeneralOutPatientDesk model) {
        ClerkingGeneralOutPatientDesk outPatientDesk = this.outPatientDeskRepository.save(model);
        this.clerkConsultationService.saveGeneralOutPatientDesk(outPatientDesk);
        return outPatientDesk;
    }

    @Override
    public ResponseEntity<Boolean> saveOutPatientDeskTemplate(OutPatientDeskDto dto) {
        if (ObjectUtils.isEmpty(dto.getTemplateName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Template Name is Required");
        }
        ClerkingGeneralOutPatientDesk desk = this.saveSession(this.mapOutPatientDeskDtoToModel(dto));
        return this.templateService.saveGeneralOutPatientDeskTemplate(desk, dto.getTemplateName());
    }

    @Override
    public OutPatientDeskDto findById(Long id) {
        Optional<ClerkingGeneralOutPatientDesk> optional = this.outPatientDeskRepository.findById(id);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return this.mapOutPatientDeskModelToDto(optional.get());
    }

    @Override
    public OutPatientDeskDto mapOutPatientDeskModelToDto(ClerkingGeneralOutPatientDesk model) {
        OutPatientDeskDto formDto = new OutPatientDeskDto();

        if (ObjectUtils.isNotEmpty(model.getId())) {
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getBackgroundHistory())) {
            formDto.setBackgroundHistory(this.backgroundHistoryService.mapToDto(model.getBackgroundHistory()));
        }
        if (ObjectUtils.isNotEmpty(model.getClinicalAssessment())) {
            formDto.setClinicalAssessment(this.clinicalAssessmentService.mapToDto(model.getClinicalAssessment()));
        }
        if (ObjectUtils.isNotEmpty(model.getOtherInformation())) {
            formDto.setOtherInformation(this.otherInformationService.mapToDto(model.getOtherInformation()));
        }
        if (ObjectUtils.isNotEmpty(model.getSpecialityUnit()) && ObjectUtils.isNotEmpty(model.getSpecialityUnit().getId())) {
            formDto.setSpecialityUnit(this.specialityUnitService.mapModelToDto(model.getSpecialityUnit()));
        }
        if (ObjectUtils.isNotEmpty(model.getPatientDetail())) {
            formDto.setPatient(this.patientDetailService.mapToPatientDto(model.getPatientDetail()));
        }
        if (model.getHasInformantDetails() && ObjectUtils.isNotEmpty(model.getInformantDetails())) {
            formDto.setHasInformantDetails(model.getHasInformantDetails());
            formDto.setInformantDetail(this.informantService.mapToDto(model.getInformantDetails()));
        }
        if (ObjectUtils.isNotEmpty(model.getPhysicalExamination())) {
            formDto.setPhysicalExamination(this.physicalService.mapToDto(model.getPhysicalExamination()));
        }
        if (ObjectUtils.isNotEmpty(model.getSystemicExamination())) {
            formDto.setSystemicExamination(this.systemicService.mapToDto(model.getSystemicExamination()));
        }
        if (ObjectUtils.isNotEmpty(model.getCardioVascularExamination())) {
            formDto.setCardioVascularForm(this.cardioVascularService.mapToDto(model.getCardioVascularExamination()));
        }
        if (ObjectUtils.isNotEmpty(model.getAbdomenExaminationDetails())) {
            formDto.setAbdomenForm(this.abdomenExaminationService.mapToDto(model.getAbdomenExaminationDetails()));
        }
        if (ObjectUtils.isNotEmpty(model.getPerineumExamination())) {
            formDto.setPerieneumForm(this.perineumService.mapToDto(model.getPerineumExamination()));
        }
        if (ObjectUtils.isNotEmpty(model.getMusculoSkeletalExamination())) {
            formDto.setMusculoSkeletalForm(this.musculoSkeletalService.mapToDto(model.getMusculoSkeletalExamination()));
        }
        if (ObjectUtils.isNotEmpty(model.getNeurologicalExamination())) {
            formDto.setNeurologicalExamination(this.neurologicalService.mapToDto(model.getNeurologicalExamination()));
        }
        if (ObjectUtils.isNotEmpty(model.getActualDiagnosis())) {
            formDto.setActualDiagnosisForm(this.actualDiagnosisService.mapToDto(model.getActualDiagnosis()));
        }
        if (ObjectUtils.isNotEmpty(model.getCapturedBy())) {
            formDto.setCapturedBy(this.userService.mapModelToDto(model.getCapturedBy()));
        }
        if (ObjectUtils.isNotEmpty(model.getCapturedFrom())) {
            formDto.setCaptureFromLocation(this.departmentService.mapModelToDto(model.getCapturedFrom()));
        }
        if (ObjectUtils.isNotEmpty(model.getIsTemplate())) {
            formDto.setIsSaveAsTemplate(model.getIsTemplate());
        }
        return formDto;
    }

    @Override
    public ClerkingGeneralOutPatientDesk mapOutPatientDeskDtoToModel(OutPatientDeskDto dto) {
        ClerkingGeneralOutPatientDesk model = new ClerkingGeneralOutPatientDesk();
        if (ObjectUtils.isNotEmpty(dto.getId())) {
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getBackgroundHistory())) {
            model.setBackgroundHistory(this.backgroundHistoryService.save(dto.getBackgroundHistory()));
        }
        if (ObjectUtils.isNotEmpty(dto.getClinicalAssessment())) {
            model.setClinicalAssessment(this.clinicalAssessmentService.save(dto.getClinicalAssessment()));
        }
        if (ObjectUtils.isNotEmpty(dto.getOtherInformation())) {
            model.setOtherInformation(this.otherInformationService.save(dto.getOtherInformation()));
        }
        if (ObjectUtils.isNotEmpty(dto.getSpecialityUnit()) && ObjectUtils.isNotEmpty(dto.getSpecialityUnit().getId())) {
            model.setSpecialityUnit(this.specialityUnitService.findOneRaw(dto.getSpecialityUnit().getId().get()));
        }
        if (ObjectUtils.isNotEmpty(dto.getPatient()) && dto.getPatient().getPatientId() != null) {
            model.setPatientDetail(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
        }

        if (dto.getHasInformantDetails() && ObjectUtils.isNotEmpty(dto.getInformantDetail())) {
            model.setHasInformantDetails(dto.getHasInformantDetails());
            model.setInformantDetails(this.informantService.save(dto.getInformantDetail()));
        }

        if (ObjectUtils.isNotEmpty(dto.getPhysicalExamination())) {
            model.setPhysicalExamination(this.physicalService.save(dto.getPhysicalExamination()));
        }
        if (ObjectUtils.isNotEmpty(dto.getSystemicExamination())) {
            model.setSystemicExamination(this.systemicService.save(dto.getSystemicExamination()));
        }
        if (ObjectUtils.isNotEmpty(dto.getCardioVascularForm())) {
            model.setCardioVascularExamination(this.cardioVascularService.save(dto.getCardioVascularForm()));
        }
        if (ObjectUtils.isNotEmpty(dto.getAbdomenForm())) {
            model.setAbdomenExaminationDetails(this.abdomenExaminationService.save(dto.getAbdomenForm()));
        }
        if (ObjectUtils.isNotEmpty(dto.getPerieneumForm())) {
            model.setPerineumExamination(this.perineumService.save(dto.getPerieneumForm()));
        }
        if (ObjectUtils.isNotEmpty(dto.getMusculoSkeletalForm())) {
            model.setMusculoSkeletalExamination(this.musculoSkeletalService.save(dto.getMusculoSkeletalForm()));
        }
        if (ObjectUtils.isNotEmpty(dto.getNeurologicalExamination())) {
            model.setNeurologicalExamination(this.neurologicalService.save(dto.getNeurologicalExamination()));
        }
        if (ObjectUtils.isNotEmpty(dto.getActualDiagnosisForm())) {
            model.setActualDiagnosis(this.actualDiagnosisService.save(dto.getActualDiagnosisForm()));
        }
        if (ObjectUtils.isNotEmpty(dto.getCapturedBy()) && dto.getCapturedBy().getId().isPresent()) {
            model.setCapturedBy(this.userService.findOneRaw(dto.getCapturedBy().getId().get()));
        }
        if (ObjectUtils.isNotEmpty(dto.getCaptureFromLocation()) && dto.getCaptureFromLocation().getId().isPresent()) {
            model.setCapturedFrom(this.departmentService.findOne(dto.getCaptureFromLocation().getId().get()));
        }
        if (ObjectUtils.isNotEmpty(dto.getIsSaveAsTemplate())) {
            model.setIsTemplate(dto.getIsSaveAsTemplate());
        }
        return model;
    }

    private void onValidateBeforeSave(OutPatientDeskDto dto) {
        if (ObjectUtils.isEmpty(dto.getCapturedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Captured By Is Required");
        }

        if (ObjectUtils.isEmpty(dto.getCaptureFromLocation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location Is Required");
        }

        if (dto.getClinicalAssessment() != null) {
            if (dto.getClinicalAssessment().getProvisionalDiagnosis() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provisional Diagnosis Is Required");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic Assessment Form Is Required");
        }
    }

}
