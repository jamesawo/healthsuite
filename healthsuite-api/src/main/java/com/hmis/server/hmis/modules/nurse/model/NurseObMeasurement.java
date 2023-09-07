package com.hmis.server.hmis.modules.nurse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_nurse_ob_measurement_data")
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class NurseObMeasurement {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight", columnDefinition="TEXT")
    private String weight;

    @Column(name = "height", columnDefinition="TEXT")
    private String height;

    @Column(name = "blood_pressure", columnDefinition="TEXT")
    private String bloodPressure;

    @Column(name = "breast_and_nipples", columnDefinition="TEXT")
    private String breastAndNipples;

    @Column(name = "abdomen", columnDefinition="TEXT")
    private String abdomen;

    @Column(name = "vaginal_examination", columnDefinition="TEXT")
    private String vaginalExamination;

    @Column(name = "other_abnormalities", columnDefinition="TEXT")
    private String otherAbnormalities;

    @Column(name = "comment", columnDefinition="TEXT")
    private String comment;

    @Column(name = "special_instructions", columnDefinition="TEXT")
    private String specialInstructions;

}
