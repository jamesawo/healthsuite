package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_cancelled_payment_data")
@NoArgsConstructor
public class CancelledPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private PatientPayment payment;

    @OneToOne
    @JoinColumn(name = "cancelled_by_user_id")
    private User cancelledBy;

    @Column(name = "cancelled_date")
    private LocalDate cancelledDate;

    @Column(name = "cancelled_time")
    private LocalTime cancelledTime;

    @OneToOne
    @JoinColumn(name = "cancelled_from_dep_id")
    private Department cancelledFrom;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "payment_type", nullable = false)
    private PaymentTypeForEnum paymentType;

    private Double cancelledGross;
    private Double cancelledNet;
    private Double cancelledDiscount;
    private Double cancelledWaived;

}
