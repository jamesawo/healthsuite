package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_systemic_examination_data")
@NoArgsConstructor
@ToString
public class ClerkSystemicExamination {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String dyspnoea;
    @Column
    private String paroxysmalNocturnalDyspnoea;
    @Column
    private String positionOfTrachea;
    @Column
    private String percussionNote;
    @Column
    private String respiratoryRate;
    @Column
    private String orthopnoea;
    @Column
    private String chestMovement;
    @Column
    private String auscultation;
}
