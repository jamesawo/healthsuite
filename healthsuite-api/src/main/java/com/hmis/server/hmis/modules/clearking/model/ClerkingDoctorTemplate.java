package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.dto.ClinicDeskEnum;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_clerk_doctor_template_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class ClerkingDoctorTemplate {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "saved_date")
    private LocalDate savedDate = LocalDate.now();

    @Column(name = "saved_time")
    private LocalTime savedTime = LocalTime.now();

    @OneToOne
    @JoinColumn(name = "saved_by")
    private User savedBy;

    @Column(name = "code")
    private String code;

    @Column(name = "desk_enum")
    private ClinicDeskEnum deskEnum;

    @OneToOne
    @JoinColumn(name = "out_patient_desk_id")
    private ClerkingGeneralOutPatientDesk outPatientDesk;

    @OneToOne
    @JoinColumn(name = "general_clerk_desk_id")
    private ClerkGeneralClerkDesk generalClerkDesk;


}
