package com.hmis.server.hmis.modules.clearking.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_physical_examination_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class ClerkPhysicalExamination {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "level_of_consciousness")
    private String levelOfConsciousness;
    @Column(name = "patient_type")
    private String patientType;
    @Column
    private String febril;
    @Column
    private String pallor;
    @Column
    private String dehydration;
    @Column
    private String cyanosis;
    @Column
    private String jaundice;
}
