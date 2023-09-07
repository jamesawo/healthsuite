package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "hmis_patient_fluid_balance_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class PatientFluidBalance extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String inputType;

    @Column
    private Double blood;

    @Column
    private Double tube;

    @Column
    private Double oral;

    @Column
    private Double iv;

    @Column
    private Double totalIntake;

    @Column
    private Double balance;

    @Column
    private Double totalOutput;

    @Column
    private Double urine;

    @Column
    private Double tubeVomit;

    @Column
    private Double drainFaeces;

    @Column
    private String outputType;

    @OneToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patientDetail;

    @OneToOne
    @JoinColumn(name = "captured_by_user_id")
    private User captureBy;

    @OneToOne
    @JoinColumn(name = "captured_from_dep_id")
    private Department capturedFrom;

    @Column
    private LocalDate date = LocalDate.now();

    @Column
    private LocalTime time = LocalTime.now();

    @Column
    private String otherInformation;
}
