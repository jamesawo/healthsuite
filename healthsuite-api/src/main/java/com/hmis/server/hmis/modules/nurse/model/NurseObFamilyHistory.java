package com.hmis.server.hmis.modules.nurse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_nurse_ob_fam_history_data")
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class NurseObFamilyHistory {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="heart_disease", columnDefinition="TEXT")
    private String heartDisease;

    @Column(name="multiple_pregnancy", columnDefinition="TEXT")
    private String multiplePregnancy;

    @Column(name="hypertension", columnDefinition="TEXT")
    private String hypertension;

    @Column(name="tuberculosis", columnDefinition="TEXT")
    private String tuberculosis;

    @Column(name="others", columnDefinition="TEXT")
    private String others;
}
