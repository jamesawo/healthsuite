package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_clinical_assessment_data")
@NoArgsConstructor
@ToString
public class ClerkClinicalAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String clinicalAssessment;
    @Column
    private String provisionalDiagnosis;
    @Column
    private String treatmentPlan;
    @Column
    private String recordInvestigationResults;
    @Column
    private String followUpNote;
}
