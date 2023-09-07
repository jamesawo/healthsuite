package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_patient_category_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor @AllArgsConstructor
public class PatientCategory extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;
    

    public PatientCategory(String title) {
        this.title = title;
    }
}
