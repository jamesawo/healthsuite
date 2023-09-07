package com.hmis.server.hmis.modules.pharmacy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.model.PaymentReceipt;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_drug_dispense_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
public class DrugDispense {
        @Id
        @GeneratedValue( strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "payment_receipt_id", unique = true)
        private PaymentReceipt receipt;

        @OneToOne
        @JoinColumn(name = "dispensed_by_user_id")
        private User dispensedBy;

        @Column(name = "date")
        private LocalDate date = LocalDate.now();

        @Column(name = "time")
        private LocalTime time = LocalTime.now();

        @OneToOne()
        @JoinColumn(name = "outlet_dep_id")
        private Department outlet;

}
