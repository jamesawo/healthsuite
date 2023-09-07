package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_religion_data")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Religion extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String code;

    @Column
    private String description;

    public Religion(Long id) {
        this.id = id;
    }

    public Religion(String name) {
        this.name = name;
    }

}
