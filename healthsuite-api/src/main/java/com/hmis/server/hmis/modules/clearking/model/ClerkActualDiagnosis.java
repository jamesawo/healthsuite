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
@Table( name = "hmis_clerk_actual_diagnosis_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class ClerkActualDiagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "other_diagnosis")
    private String otherDiagnosis;

    @Type( type = "jsonb" )
    @Column( columnDefinition = "jsonb" )
    private List<String> diseasesList;
}
