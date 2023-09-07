package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_pharmacy_patient_category_type_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class PharmacyPatientCategoryType extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public PharmacyPatientCategoryType(String name) {
        this.name = name;
    }
}
