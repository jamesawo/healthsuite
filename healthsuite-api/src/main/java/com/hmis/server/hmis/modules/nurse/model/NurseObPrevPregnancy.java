package com.hmis.server.hmis.modules.nurse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.Gender;
import com.hmis.server.hmis.modules.clearking.dto.YesNoEnum;
import com.hmis.server.hmis.modules.nurse.dto.PregnancyOutComeEnum;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "hmis_nurse_prev_pregnancy_data")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NurseObPrevPregnancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duration_of_pregnancy")
    private int durationOfPregnancy;

    @Column(name = "outcome_enum")
    private PregnancyOutComeEnum outcome;

    @OneToOne
    @JoinColumn(name = "gender_id")
    private Gender sex;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "birth_weight")
    private int birthWeight;

    @Column(name = "alive")
    private YesNoEnum alive;

    @Column(name = "comment")
    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "obstetric_history_id")
    private NurseObstetricHistory obstetricHistory;
}
