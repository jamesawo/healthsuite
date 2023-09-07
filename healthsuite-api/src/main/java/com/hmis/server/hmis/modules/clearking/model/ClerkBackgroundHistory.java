package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_clerk_background_history_data")
@NoArgsConstructor
@ToString
public class ClerkBackgroundHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="presenting_complaint", columnDefinition = "TEXT")
    private String presentingComplaint;

    @Column(name="history_of_presenting_complaint", columnDefinition = "TEXT")
    private String historyOfPresentingComplaint;

    @Column(name="review_of_system", columnDefinition = "TEXT")
    private String reviewOfSystem;

    @Column(name="past_medical_and_surgical_history", columnDefinition = "TEXT")
    private String pastMedicalAndSurgicalHistory;

    @Column(name="psychiatric_history", columnDefinition = "TEXT")
    private String psychiatricHistory;

    @Column(name = "obstetrics_and_gynaecology_history", columnDefinition = "TEXT")
    private String obstetricsAndGynaecologyHistory;

    @Column(name="paediatric_history", columnDefinition = "TEXT")
    private String paediatricHistory;

    @Column(name="drug_history", columnDefinition = "TEXT")
    private String drugHistory;

    @Column(name="immunization_history", columnDefinition = "TEXT")
    private String immunizationHistory;

    @Column(name="travel_history", columnDefinition = "TEXT")
    private String travelHistory;

    @Column(name="family_history", columnDefinition = "TEXT")
    private String familyHistory;
}
