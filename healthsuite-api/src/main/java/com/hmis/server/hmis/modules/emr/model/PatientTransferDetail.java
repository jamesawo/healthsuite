package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_patient_transfer_detail_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class PatientTransferDetail extends Auditable<String> {
    /* the hospital patient was transferred from */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String hospitalTransferFrom;

    @Column(nullable = true)
    private LocalDate dateOfTransfer;

    @Column(nullable = true)
    private  String hospitalAddress;

    @Column(nullable = true)
    private String authorizedDoctor;

    @Column(nullable = true)
    private  String reasonForTransfer;
}
