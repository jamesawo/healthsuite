package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Entity
@Table(name = "hmis_patient_number_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class PatientNumber extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String suffix;

    @Column
    private String prefix;

    @Column
    private AtomicInteger number;

    @Column
    private Boolean isAllocated;

}
