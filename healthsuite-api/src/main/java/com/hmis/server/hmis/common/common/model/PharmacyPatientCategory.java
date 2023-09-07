package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_pharmacy_patient_category_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class PharmacyPatientCategory extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne
    private PharmacyPatientCategoryType pharmacyPatientCategoryType;

    @OneToOne
    private PaymentMethod paymentMethod;

    public PharmacyPatientCategory(Long id){
        this.id = id;
    }
}
