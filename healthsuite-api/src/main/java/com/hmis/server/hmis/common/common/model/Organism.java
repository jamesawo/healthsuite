package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_organism_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class Organism extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String code;

    @Column
    private String description;

    @OneToMany(mappedBy = "organism")
    List<Antibiotics> antibiotics;

}
