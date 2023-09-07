package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table( name = "hmis_clerk_doctor_request_data")
@NoArgsConstructor
@ToString
public class ClerkDoctorRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "patient_detail_id", nullable = false)
    private PatientDetail patient;

    @OneToOne
    @JoinColumn(name = "doctor_user_id", nullable = false)
    private User doctor;

    @OneToOne
    @JoinColumn(name = "location_department_id", nullable = false)
    private Department department;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @OneToOne
    @JoinColumn( name = "clerk_request_drug_id")
    private ClerkRequestDrug drugRequest;

    @OneToOne
    @JoinColumn( name = "clerk_request_lab_id")
    private ClerkRequestLab labRequest;

    @OneToOne
    @JoinColumn( name = "clerk_radiology_request_id")
    private ClerkRequestRadiology radiologyRequest;

}
