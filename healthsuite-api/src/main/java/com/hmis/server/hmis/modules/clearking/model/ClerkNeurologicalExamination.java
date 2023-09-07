package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_neurological_examination_data")
@NoArgsConstructor
@ToString
public class ClerkNeurologicalExamination {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String gait;
    @Column
    private String abnormalMovement;

}
