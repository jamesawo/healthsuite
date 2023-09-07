package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_surgery_data")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Surgery extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String code;

    public Surgery(String name, String code) {
        this.name = name;
        this.code = code;
    }

}
