package com.hmis.server.hmis.modules.nurse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_nurse_ob_previous_medical_data")
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class NurseObPreviousMedical {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "heart_disease", columnDefinition="TEXT")
    private String heartDisease;

    @Column(name = "chest_disease", columnDefinition="TEXT")
    private String chestDisease;

    @Column(name = "kidney_disease", columnDefinition="TEXT")
    private String kidneyDisease;

    @Column(name = "blood_transfusion", columnDefinition="TEXT")
    private String bloodTransfusion;

    @Column(name = "others", columnDefinition="TEXT")
    private String others;
}
