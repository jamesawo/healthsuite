package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrugItem;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.dto.DrugChartTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "hmis_nurse_patient_drug_chart_data")
@NoArgsConstructor
@ToString
public class NursePatientDrugChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "time")
    private LocalTime time = LocalTime.now();

    @OneToOne
    @JoinColumn(name = "administered_drug_request_item")
    private ClerkRequestDrugItem administeredDrugRequestItem;

    @Column(name = "type_enum", nullable = false)
    private DrugChartTypeEnum typeEnum;

    @OneToOne
    @JoinColumn(name = "performed_by_user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Department location;

    @OneToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patient;
}
