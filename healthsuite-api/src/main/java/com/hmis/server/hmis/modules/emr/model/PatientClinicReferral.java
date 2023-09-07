package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "hmis_patient_clinic_referral_data")
@NoArgsConstructor
@ToString
public class PatientClinicReferral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @OneToOne
    @JoinColumn(name = "referred_by_user_id")
    private User referredBy;

    @OneToOne
    @JoinColumn(name = "referred_to_dep_id")
    private Department referredToClinic;

    @OneToOne
    @JoinColumn(name = "referred_from_dep_id")
    private Department referredFromClinic;

    @OneToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patient;

    @Column(name = "referral_notes")
    private String referralNotes;
}

