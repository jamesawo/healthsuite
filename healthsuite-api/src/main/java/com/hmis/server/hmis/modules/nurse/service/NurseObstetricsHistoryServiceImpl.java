package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.NurseObstetricsHistoryDto;
import com.hmis.server.hmis.modules.nurse.model.*;
import com.hmis.server.hmis.modules.nurse.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Slf4j
public class NurseObstetricsHistoryServiceImpl {
    private final NurseObPrevPregnancyRepository prevPregnancyRepository;
    private final NurseObstetricHistoryRepository obstetricHistoryRepository;
    private final NurseObPreviousMedicalRepository previousMedicalRepository;
    private final NurseObPhysicalExamRepository physicalExamRepository;
    private final NurseObMeasurementRepository measurementRepository;
    private final NurseObHisOfPresentPregnancyRepository presentPregnancyRepository;
    private final NurseObGeneralFormRepository generalFormRepository;
    private final NurseObFamilyHistoryRepository familyHistoryRepository;
    private final NurseObDeliveryInstructionRepository instructionRepository;
    private final DepartmentServiceImpl departmentService;
    private final PatientDetailServiceImpl patientDetailService;
    private final UserServiceImpl userService;
    private final PatientClerkActivityService activityService;

    @Autowired
    public NurseObstetricsHistoryServiceImpl(
            NurseObPrevPregnancyRepository prevPregnancyRepository,
            NurseObstetricHistoryRepository obstetricHistoryRepository,
            NurseObPreviousMedicalRepository previousMedicalRepository,
            NurseObPhysicalExamRepository physicalExamRepository,
            NurseObMeasurementRepository measurementRepository,
            NurseObHisOfPresentPregnancyRepository presentPregnancyRepository,
            NurseObGeneralFormRepository generalFormRepository,
            NurseObFamilyHistoryRepository familyHistoryRepository,
            NurseObDeliveryInstructionRepository instructionRepository,
            @Lazy DepartmentServiceImpl departmentService,
            @Lazy PatientDetailServiceImpl patientDetailService,
            UserServiceImpl userService,
            @Lazy PatientClerkActivityService activityService
    ) {
        this.prevPregnancyRepository = prevPregnancyRepository;
        this.obstetricHistoryRepository = obstetricHistoryRepository;
        this.previousMedicalRepository = previousMedicalRepository;
        this.physicalExamRepository = physicalExamRepository;
        this.measurementRepository = measurementRepository;
        this.presentPregnancyRepository = presentPregnancyRepository;
        this.generalFormRepository = generalFormRepository;
        this.familyHistoryRepository = familyHistoryRepository;
        this.instructionRepository = instructionRepository;
        this.departmentService = departmentService;
        this.patientDetailService = patientDetailService;
        this.userService = userService;
        this.activityService = activityService;
    }

    @Transactional
    public ResponseEntity<MessageDto> saveObstetricsHistory(NurseObstetricsHistoryDto dto) {
        try {
            PatientDetail patient = this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId());
            NurseObGeneralForm nurseObGeneralForm = this.saveGeneralFrom(dto.getGeneralForm());
            NurseObPreviousMedical nurseObPreviousMedical = this.savePrevMedicalHistory(dto.getPreviousMedicalHistory());
            NurseObFamilyHistory nurseObFamilyHistory = this.saveFamilyHistory(dto.getFamilyHistory());
            NurseObHisOfPresentPregnancy historyOfPresentPregnancy = this.saveHistoryOfPresentPregnancy(dto.getHistoryOfPresentPregnancy());
            NurseObPhysicalExam physicalExam = this.savePhyscialExamination(dto.getPhysicalExamination());
            NurseObMeasurement measurement = this.saveMeasurement(dto.getMeasurement());
            NurseObDeliveryInstruction nurseObDeliveryInstruction = this.saveDeliveryInstruction(dto.getDeliveryInstruction());
            User user = this.userService.findOneRaw(dto.getClerkedBy().getId().get());
            Department location = this.departmentService.findOne(dto.getLocation().getId().get());

            NurseObstetricHistory history = new NurseObstetricHistory();
            history.setPatientDetail(patient);
            history.setGeneralForm(nurseObGeneralForm);
            history.setPreviousMedical(nurseObPreviousMedical);
            history.setFamilyHistory(nurseObFamilyHistory);
            history.setHisOfPresentPregnancy(historyOfPresentPregnancy);
            history.setPhysicalExam(physicalExam);
            history.setMeasurement(measurement);
            history.setInstruction(nurseObDeliveryInstruction);
            history.setByUser(user);
            history.setLocation(location);
            NurseObstetricHistory savedHistory = this.obstetricHistoryRepository.save(history);
            List<NurseObPrevPregnancy> previousPregnancies = dto.getPreviousPregnancies();
            this.savePreviousPregnancies(previousPregnancies, savedHistory);
            this.activityService.saveObstetricsHistory(savedHistory);

            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("SAVED SUCCESSFULLY."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    private NurseObGeneralForm saveGeneralFrom(NurseObGeneralForm generalForm) {
        return this.generalFormRepository.save(generalForm);
    }

    private NurseObPreviousMedical savePrevMedicalHistory(NurseObPreviousMedical previousMedicalHistory) {
        return this.previousMedicalRepository.save(previousMedicalHistory);
    }

    private NurseObFamilyHistory saveFamilyHistory(NurseObFamilyHistory familyHistory) {
        return this.familyHistoryRepository.save(familyHistory);
    }

    private NurseObHisOfPresentPregnancy saveHistoryOfPresentPregnancy(NurseObHisOfPresentPregnancy historyOfPresentPregnancy) {
        return this.presentPregnancyRepository.save(historyOfPresentPregnancy);
    }

    private NurseObPhysicalExam savePhyscialExamination(NurseObPhysicalExam physicalExamination) {
        return this.physicalExamRepository.save(physicalExamination);
    }

    private NurseObMeasurement saveMeasurement(NurseObMeasurement measurement) {
        return this.measurementRepository.save(measurement);
    }

    private NurseObDeliveryInstruction saveDeliveryInstruction(NurseObDeliveryInstruction deliveryInstruction) {
        return this.instructionRepository.save(deliveryInstruction);
    }

    private void savePreviousPregnancies(List<NurseObPrevPregnancy> previousPregnancies, NurseObstetricHistory savedHistory) {
        if (!previousPregnancies.isEmpty()) {
            for (NurseObPrevPregnancy previousPregnancy : previousPregnancies) {
                previousPregnancy.setObstetricHistory(savedHistory);
                this.prevPregnancyRepository.save(previousPregnancy);
            }
        }
    }
}
