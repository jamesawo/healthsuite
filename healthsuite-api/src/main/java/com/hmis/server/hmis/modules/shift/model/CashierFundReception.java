package com.hmis.server.hmis.modules.shift.model;

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
@Table( name = "hmis_cashier_fund_reception_data")
@NoArgsConstructor
@ToString
public class CashierFundReception {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date = LocalDate.now();

    @OneToOne
    @JoinColumn(name = "received_by_user_id")
    private User receivedBy;

    @Column(name = "time")
    private LocalTime time = LocalTime.now();

    @OneToOne
    @JoinColumn(name = "cashier_shift_id", nullable = false)
    private CashierShift shift;

    @OneToOne
    @JoinColumn(name = "location_department_id")
    private Department location;
}
