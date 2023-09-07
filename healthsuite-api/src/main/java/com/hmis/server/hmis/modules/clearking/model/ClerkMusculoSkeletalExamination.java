package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_musculo_skeletal_examination_data")
@NoArgsConstructor
@ToString
public class ClerkMusculoSkeletalExamination {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String muscleBulk;
    @Column
    private String spasticity;
    @Column
    private String reflexe;
    @Column
    private String tone;
    @Column
    private String rigidity;
}
