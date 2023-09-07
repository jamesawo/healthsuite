package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.dto.WaitingStatusEnum;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_doctor_waiting_list_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class DoctorWaitingList {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn( name = "patient_detail", nullable = false, unique = true )
    private PatientDetail patientDetail;

    @Column(name = "waiting_status", nullable = false)
    private String waitingStatus = WaitingStatusEnum.WAITING.name();

    @OneToOne
    @JoinColumn(name = "clinic_department_id", nullable = false)
    private Department clinic;

    @OneToOne
    @JoinColumn(name = "doctor_user_id")
    private User doctor;

    @Column(name = "recorded_date_time")
    private LocalDateTime dateTime = LocalDateTime.now();

    @Column( name = "date" )
    private LocalDate date = LocalDate.now();

    @Column( name = "time" )
    private LocalTime time = LocalTime.now();

}
