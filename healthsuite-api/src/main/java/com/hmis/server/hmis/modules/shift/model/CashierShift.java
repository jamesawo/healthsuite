package com.hmis.server.hmis.modules.shift.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.shift.dto.ShiftCloseTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_cashier_shift_data")
@ToString
public class CashierShift {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate = LocalDate.now();

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime = LocalTime.now();

    @Column(name = "receipt_count")
    private int receiptCount = 0;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @OneToOne
    @JoinColumn(name = "closed_by_user_id")
    private User closedByUser;

    @OneToOne
    @JoinColumn(name = "cashier_user_id")
    private User cashier;

    @OneToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "close_type_enum")
    private ShiftCloseTypeEnum closeTypeEnum;

    @OneToMany(mappedBy = "cashierShift", fetch = FetchType.LAZY)
    @Column
    private List<PatientPayment> payments;

    @Column(name = "is_active", nullable = false)
    private Boolean  isActive = true;

    @Column(name = "is_closed_by_cashier")
    private Boolean isClosedByCashier = false;

    @Column(name = "is_reconciled", nullable = false)
    private Boolean isFundReceived = false;

    @Column(name = "is_compiled", nullable = false)
    private Boolean isShitCompiled = false;

    @ManyToOne
    @JoinColumn(name = "cashier_compiled_shift_id")
    private CashierCompiledShift compiledShift;

    @OneToOne
    @JoinColumn(name = "fund_reception_id")
    private CashierFundReception fundReception;

    public CashierShift(Long id) {
        this.id = id;
    }

    public CashierShift() {
    }

}
