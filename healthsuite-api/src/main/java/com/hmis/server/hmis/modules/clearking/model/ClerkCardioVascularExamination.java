package com.hmis.server.hmis.modules.clearking.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_clerk_cardio_vascular_examination_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class ClerkCardioVascularExamination {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String pulseRate;
    @Column
    private String bloodPressure;
    @Column
    private String jugularVenousPressure;
    @Column
    private String apexBeat;

    @Type( type = "jsonb" )
    @Column( columnDefinition = "jsonb" )
    private List<String> heartSound;
}
