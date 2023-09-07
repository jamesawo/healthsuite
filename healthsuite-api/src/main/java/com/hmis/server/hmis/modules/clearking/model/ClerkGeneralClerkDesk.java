package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_clerk_general_desk_data")
@NoArgsConstructor
@ToString
public class ClerkGeneralClerkDesk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private PatientDetail patientDetail;

    @OneToOne
    @JoinColumn(name = "clerked_by")
    private User clerkedBy;

    @OneToOne
    @JoinColumn(name = "clinic_dep_id")
    private Department clinic;

    @OneToOne
    @JoinColumn(name = "consultant_id")
    private User consultant;

    @OneToOne
    @JoinColumn(name = "speciality_unit_id")
    private SpecialityUnit specialityUnit;

    @Column(name = "has_informant_detail")
    private Boolean hasInformantDetail;

    @OneToOne
    @JoinColumn(name = "informant_detail_id")
    private ClerkInformantDetails informantDetails;

    @Column(name = "follow_up_note", columnDefinition = "TEXT")
    private String followUpNote;

    @Column(name = "provisional_diagnosis", columnDefinition = "TEXT")
    private String provisionalDiagnosis;

    @OneToOne
    @JoinColumn(name = "back_ground_history_id")
    private ClerkBackgroundHistory backgroundHistory;

    @OneToOne
    @JoinColumn(name = "actual_diagnosis_id")
    private ClerkActualDiagnosis actualDiagnosis;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "time")
    private LocalTime time = LocalTime.now();

}
