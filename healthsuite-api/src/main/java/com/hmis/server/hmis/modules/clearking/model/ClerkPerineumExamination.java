package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_Clerk_perineum_examination_data")
@NoArgsConstructor
@ToString
public class ClerkPerineumExamination {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String chaperone;
    @Column
    private String  externalGenitalia;
    @Column
    private String  perRectumExamination;
    @Column
    private String anyOtherLesions;
    @Column
    private String vaginalExamination;
}
