package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_clerk_general_out_patient_desk_data")
@NoArgsConstructor
@ToString
public class ClerkingGeneralOutPatientDesk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="other_information_id")
    private ClerkOtherInformation otherInformation;

    @OneToOne
    @JoinColumn(name = "special_unit_id")
    private SpecialityUnit specialityUnit;

    @OneToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patientDetail;

    @OneToOne
    @JoinColumn(name = "patient_informant_details_id")
    private ClerkInformantDetails informantDetails;

    @OneToOne
    @JoinColumn(name = "patient_background_history_id")
    private ClerkBackgroundHistory backgroundHistory;

    @OneToOne
    @JoinColumn(name = "patient_clinic_assessment_id")
    private ClerkClinicalAssessment clinicalAssessment;

    @OneToOne
    @JoinColumn(name = "physical_examination_id")
    private ClerkPhysicalExamination physicalExamination;

    @OneToOne
    @JoinColumn(name = "systemic_examination_id")
    private ClerkSystemicExamination systemicExamination;

    @OneToOne
    @JoinColumn(name = "cardio_vascular_examination_id")
    private ClerkCardioVascularExamination cardioVascularExamination;

    @OneToOne
    @JoinColumn(name = "abdomen_examination_id")
    private ClerkAbdomenExaminationDetails abdomenExaminationDetails;

    @OneToOne
    @JoinColumn(name = "perineum_examination_id")
    private ClerkPerineumExamination perineumExamination;

    @OneToOne
    @JoinColumn(name = "musculo_skeletal_examination_id")
    private ClerkMusculoSkeletalExamination musculoSkeletalExamination;

    @OneToOne
    @JoinColumn(name = "neurological_examination_id")
    private ClerkNeurologicalExamination neurologicalExamination;

    @OneToOne
    @JoinColumn(name = "actual_diagnosis_id")
    private ClerkActualDiagnosis actualDiagnosis;

    @OneToOne
    @JoinColumn(name = "capture_user_id", nullable = false)
    private User capturedBy;

    @OneToOne
    @JoinColumn(name = "location_department_id", nullable = false )
    private Department capturedFrom;

    @Column(name = "is_template")
    private Boolean isTemplate = false;

    @Column(name = "has_informant_details")
    private Boolean hasInformantDetails = false;
}
