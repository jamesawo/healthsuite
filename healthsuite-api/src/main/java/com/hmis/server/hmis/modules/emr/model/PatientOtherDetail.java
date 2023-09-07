package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_patient_other_details_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class PatientOtherDetail extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean accessToTelephone;

    @Column
    private Boolean accessToInternet;

    @Column
    private Boolean accessToElectricity;

    @Column
    private Boolean accessToCleanWater;

    @Column
    private Boolean accessToGoodFood;

}

