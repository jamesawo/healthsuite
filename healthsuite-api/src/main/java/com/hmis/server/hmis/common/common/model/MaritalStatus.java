package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_marital_status_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class MaritalStatus extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public MaritalStatus(Long id) {
        this.id = id;
    }

    public MaritalStatus(String name) {
        this.name = name;
    }

}
