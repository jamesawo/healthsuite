package com.hmis.server.hmis.modules.shift.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_cashier_compiled_shift_data")
@NoArgsConstructor
@ToString
public class CashierCompiledShift {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "compiled_date")
    private LocalDate compiledDate = LocalDate.now();

    @Column(name = "compiled_time")
    private LocalTime compiledTime = LocalTime.now();

    @OneToOne
    @JoinColumn(name = "compiled_by_user_id")
    private User compiledBy;

    @Column(name = "code", unique = true)
    private String code;

    @OneToMany(mappedBy = "compiledShift")
    @Column
    private List<CashierShift> cashierShifts;

    @OneToOne
    @JoinColumn(name = "location_department")
    private Department location;

    public CashierCompiledShift(Long id) {
        this.id = id;
    }
}
