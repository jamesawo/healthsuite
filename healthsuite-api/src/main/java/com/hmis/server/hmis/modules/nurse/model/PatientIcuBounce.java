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
@Table( name = "hmis_patient_fluid_balance_data")
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
@ToString
public class PatientIcuBounce extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String reasonForBounceBack;

    @OneToOne
    @JoinColumn(name = "captured_from_dep_id")
    private Department capturedFrom;

    @OneToOne
    @JoinColumn(name = "captured_by_user_id")
    private User captureBy;

    @OneToOne
    @JoinColumn(name ="patient_detail_id")
    private PatientDetail patientDetail;

    @Column
    private LocalDate date = LocalDate.now();

    @Column
    private LocalTime time = LocalTime.now();

}
