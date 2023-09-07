package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_abdomen_examination_data")
@NoArgsConstructor
@ToString
public class ClerkAbdomenExaminationDetails {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String movesWithRespiration;
    @Column
    private String distended;
    @Column
    private String visiblePeripheralVein;
    @Column
    private String scarificationMarks;
    @Column
    private String shape;
    @Column
    private String hanialOrificesIntact;
    @Column
    private String palpationLiverEnlargement;
    @Column
    private String palpationKidneyEnlargement;
    @Column
    private String palpationSpleenEnlargement;
    @Column
    private String palpationOtherMasses;
    @Column
    private String ascultationsBruits;
    @Column
    private String ascitis;
}
