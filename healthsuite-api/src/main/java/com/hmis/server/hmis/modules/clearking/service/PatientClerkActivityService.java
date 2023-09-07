package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.ClerkPatientActivityEnum;
import com.hmis.server.hmis.modules.clearking.model.*;
import com.hmis.server.hmis.modules.clearking.repository.ClerkPatientActivityRepository;
import com.hmis.server.hmis.modules.emr.model.PatientClinicReferral;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import com.hmis.server.hmis.modules.nurse.model.NurseNote;
import com.hmis.server.hmis.modules.nurse.model.NurseObstetricHistory;
import com.hmis.server.hmis.modules.nurse.model.NursePatientDrugChart;
import com.hmis.server.hmis.modules.nurse.model.PatientVitalSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.hmis.server.hmis.modules.clearking.dto.ClerkPatientActivityEnum.*;

@Service
public class PatientClerkActivityService {
    private final ClerkPatientActivityRepository activityRepository;

    @Autowired
    public PatientClerkActivityService(
            ClerkPatientActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void savePatientVitalSignActivity(PatientVitalSign vitalSign) {
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(VITAL_SIGN);
        activity.setPatient(vitalSign.getPatient());
        activity.setVitalSign(vitalSign);
        this.activityRepository.save(activity);
    }

    public void saveWardTransferActivity(ClerkPatientWardTransfer transfer){
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(WARD_TRANSFER);
        activity.setPatient(transfer.getPatient());
        activity.setWardTransfer(transfer);
        this.activityRepository.save(activity);
    }

    public void saveNurseNoteActivity(NurseNote note) {
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(NURSE_NOTE);
        activity.setPatient(note.getPatient());
        activity.setNote(note);
        this.activityRepository.save(activity);
    }

    public void saveLabRequestActivity(ClerkRequestLab lab){
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(LAB_REQUEST);
        activity.setPatient(lab.getPatientDetail());
        activity.setLabRequest(lab);
        this.activityRepository.save(activity);
    }

    public void saveDrugRequestActivity(ClerkRequestDrug drugRequest){
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(DRUG_REQUEST);
        activity.setPatient(drugRequest.getPatientDetail());
        activity.setDrugRequest(drugRequest);
        this.activityRepository.save(activity);
    }

    public void saveDrugChartActivity(NursePatientDrugChart chart){
        //drug chart activity such as drug chart, drug administration ..
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(DRUG_CHART);
        activity.setPatient(chart.getPatient());
        activity.setDrugChart(chart);
        this.activityRepository.save(activity);
    }

    public void saveRadiologyRequestActivity(ClerkRequestRadiology radiology){
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(RADIOLOGY_REQUEST);
        activity.setPatient(radiology.getPatientDetail());
        activity.setRadiologyRequest(radiology);
        this.activityRepository.save(activity);
    }

    public void saveObstetricsHistory(NurseObstetricHistory savedHistory){
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(OBSTETRICS_HISTORY);
        activity.setPatient(savedHistory.getPatientDetail());
        activity.setObstetricHistory(savedHistory);
        this.activityRepository.save(activity);
    }

    public void saveConsultationActivity(ClerkConsultation save, PatientDetail patientDetail) {
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(CONSULTATION);
        activity.setPatient(patientDetail);
        activity.setConsultation(save);
        this.activityRepository.save(activity);
    }

    public void saveClinicTransfer(PatientClinicReferral clinicReferral) {
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(CLINIC_TRANSFER);
        activity.setPatient(clinicReferral.getPatient());
        activity.setClinicTransfer(clinicReferral);
        this.activityRepository.save(activity);
    }


    public void saveLabTestResultPreparation(LabTestPreparation testPreparation) {
        ClerkPatientActivity activity = new ClerkPatientActivity();
        activity.setActivityEnum(LAB_RESULT);
        activity.setPatient(testPreparation.getPatientDetail());
        activity.setLabTestPreparation(testPreparation);
        this.activityRepository.save(activity);
    }

    public List<ClerkPatientActivity> findPatientSpecificRecords(
            PatientDetail patientDetail,
            LocalDate end, LocalDate start,
            List<ClerkPatientActivityEnum> activityEnums){
        return this.activityRepository.findAllByPatientAndActivityEnumIsInAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateDescTimeDesc(
                patientDetail, activityEnums, end, start
        );
    }

    public List<ClerkPatientActivity> findPatientAllRecords(
            PatientDetail patientDetail,
            LocalDate end, LocalDate start){
        return this.activityRepository
                        .findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateDescTimeDesc(
                patientDetail, end, start
        );
    }


}
