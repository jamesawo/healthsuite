package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_nurse_obstetric_history_data")
@NoArgsConstructor
@ToString
public class NurseObstetricHistory {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private PatientDetail patientDetail;

    @OneToOne
    @JoinColumn(name = "general_form_id")
    private NurseObGeneralForm generalForm;

    @OneToOne
    @JoinColumn(name = "ob_prev_medical_id")
    private NurseObPreviousMedical previousMedical;

    @OneToOne
    @JoinColumn(name = "ob_family_history_id")
    private NurseObFamilyHistory familyHistory;

    @OneToMany(mappedBy = "obstetricHistory")
    private List<NurseObPrevPregnancy> prevPregnancies;

    @OneToOne
    @JoinColumn(name = "ob_present_pregnancy_id")
    private NurseObHisOfPresentPregnancy hisOfPresentPregnancy;

    @OneToOne
    @JoinColumn(name = "ob_physical_exam_id")
    private NurseObPhysicalExam physicalExam;

    @OneToOne
    @JoinColumn(name = "ob_measurement_id")
    private NurseObMeasurement measurement;

    @OneToOne
    @JoinColumn(name = "ob_instruction_id")
    private NurseObDeliveryInstruction instruction;

    @OneToOne
    @JoinColumn(name = "location_dep_id")
    private Department location;

    @OneToOne
    @JoinColumn(name = "by_user_id")
    private User byUser;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "time")
    private LocalTime time = LocalTime.now();
}
