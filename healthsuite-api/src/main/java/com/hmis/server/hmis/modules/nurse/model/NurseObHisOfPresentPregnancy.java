package com.hmis.server.hmis.modules.nurse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_nurse_ob_present_pregnancy_data")
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class NurseObHisOfPresentPregnancy {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bleeding", columnDefinition="TEXT")
    private String bleeding;

    @Column(name = "discharge", columnDefinition="TEXT")
    private String discharge;

    @Column(name = "urinary_symptom", columnDefinition="TEXT")
    private String urinarySymptom;

    @Column(name = "swelling_of_ankles", columnDefinition="TEXT")
    private String swellingOfAnkles;

    @Column(name = "other_symptoms", columnDefinition="TEXT")
    private String otherSymptoms;
}
