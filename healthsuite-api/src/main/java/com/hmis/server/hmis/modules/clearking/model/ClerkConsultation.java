package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.modules.clearking.dto.ConsultationDeskEnum;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_consultation_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class ClerkConsultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "out_patient_desk_id")
    private ClerkingGeneralOutPatientDesk outPatientDesk;

    @OneToOne
    @JoinColumn(name = "general_clerk_desk_id")
    private ClerkGeneralClerkDesk generalClerkDesk;

    @Column(name = "desk_enum", nullable = false)
    private ConsultationDeskEnum deskEnum;

}
