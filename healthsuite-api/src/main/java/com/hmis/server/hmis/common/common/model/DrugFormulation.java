package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hmis_drug_formulation_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DrugFormulation extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    public DrugFormulation(Long id) {
        this.id = id;
    }

    public DrugFormulation(String name) {
        this.name = name;
    }

}
