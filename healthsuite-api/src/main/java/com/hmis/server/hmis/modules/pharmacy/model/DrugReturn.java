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
@Table( name = "hmis_drug_return_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
public class DrugReturn {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "payment_receipt_id", unique = true)
    private PaymentReceipt paymentReceipt;

    @OneToOne
    @JoinColumn(name = "returned_user_id")
    private User returnedByUser;

    @Column(name = "returned_date")
    private LocalDate returnedDate = LocalDate.now();

    @Column(name = "returned_time")
    private LocalTime returnedTime = LocalTime.now();

    @OneToOne()
    @JoinColumn(name = "outlet_dep_id")
    private Department outlet;

    @Column(name = "item_count")
    private int itemCount;

    @Column(name = "receipt_net_amount")
    private Double receiptNetAmount;

    @Column(name = "comment")
    private String comment;

}
