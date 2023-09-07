package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hmis_drug_classification_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DrugClassification extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column
    private String code;

    public DrugClassification(Long id) {
        this.id = id;
    }

    public DrugClassification(String name) {
        this.name = name;
    }

}
