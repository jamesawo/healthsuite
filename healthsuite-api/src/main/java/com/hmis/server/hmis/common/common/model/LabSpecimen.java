package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_lab_specimen_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class LabSpecimen extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String code;

    @Column
    private String description;

    public LabSpecimen(Long id) {
        this.id = id;
    }

    public LabSpecimen(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
