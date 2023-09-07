package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_patient_adjusted_bill_data" )
@NoArgsConstructor
public class PatientAdjustedBill {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_bill_id", nullable = false)
    private PatientBill patientBill;

    @OneToOne
    @JoinColumn(name = "adjusted_by_user_id")
    private User adjustedBy;

    @OneToOne
    @JoinColumn(name = "location_dep_id")
    private Department location;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "new_net_total")
    private Double newNetTotal;

    @Column(name = "old_net_Total")
    private Double oldNetTotal;

    @Column(name = "comment")
    private String comment;

}
