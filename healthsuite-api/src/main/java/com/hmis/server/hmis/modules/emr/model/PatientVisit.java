package com.hmis.server.hmis.modules.emr.model;

import javax.persistence.*;

import com.hmis.server.hmis.modules.emr.dto.PatientRevisitCategoryEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "hmis_patient_visit_data" ) // patient_active_visit
@NoArgsConstructor
public class PatientVisit {
    // create visit record for patient on admission

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private PatientVisitHistory revisitHistory;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private PatientDetail patient;

    @Column(name = "revisit_category_enum")
    private PatientRevisitCategoryEnum revisitCategoryEnum;
}
