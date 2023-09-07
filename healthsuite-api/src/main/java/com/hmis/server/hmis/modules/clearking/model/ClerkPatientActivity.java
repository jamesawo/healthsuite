package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.modules.clearking.dto.ClerkPatientActivityEnum;
import com.hmis.server.hmis.modules.emr.model.PatientClinicReferral;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientVisit;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import com.hmis.server.hmis.modules.lab.model.LabTestResult;
import com.hmis.server.hmis.modules.nurse.model.NurseNote;
import com.hmis.server.hmis.modules.nurse.model.NurseObstetricHistory;
import com.hmis.server.hmis.modules.nurse.model.NursePatientDrugChart;
import com.hmis.server.hmis.modules.nurse.model.PatientVitalSign;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_clerk_patient_activity_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class ClerkPatientActivity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "visit_id")
    private PatientVisit visit;

    @ManyToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patient;

    @Column(name = "date", nullable = false)
    private LocalDate date =  LocalDate.now();

    @Column(name = "time", nullable = false)
    private LocalTime time = LocalTime.now();

    @Column(name = "activity_enum")
    private ClerkPatientActivityEnum activityEnum;

    @OneToOne
    @JoinColumn(name = "vital_sign_id")
    private PatientVitalSign vitalSign;

    @OneToOne
    @JoinColumn(name = "ward_transfer_id")
    private ClerkPatientWardTransfer wardTransfer;

    @OneToOne
    @JoinColumn(name = "nurse_note_id")
    private NurseNote note;

    @OneToOne
    @JoinColumn(name = "drug_request_id")
    private ClerkRequestDrug drugRequest;

    @OneToOne
    @JoinColumn(name = "radiology_request_id")
    private ClerkRequestRadiology radiologyRequest;

    @OneToOne
    @JoinColumn(name = "lab_request_id")
    private ClerkRequestLab labRequest;

    @OneToOne
    @JoinColumn(name = "obstetrics_history_id")
    private NurseObstetricHistory obstetricHistory;

    @OneToOne
    @JoinColumn(name = "consultation_id")
    private ClerkConsultation consultation;

    @OneToOne
    @JoinColumn(name = "patient_clinic_referral_id")
    private PatientClinicReferral clinicTransfer;

    @OneToOne
    @JoinColumn(name = "nurse_drug_chart_id")
    private NursePatientDrugChart drugChart;

    @OneToOne
    @JoinColumn(name = "lab_test_preparation_id")
    private LabTestPreparation labTestPreparation;

    /*
    private ClerkOperationNote operationNote;
    private ClerkActualDiagnosis diagnosis;
    private ClerkPatientUploadFile uploadFile;
    private NurseAncFollowUp ancFollowUpVisit;
    private XrayReport patientXrayReport;
    private RadiologyReport patientRadiologyReport;
     */


}
