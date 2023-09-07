package com.hmis.server.hmis.modules.emr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;


@Data
@Entity
@Table(name="hmis_patient_passport")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PatientImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(unique = true, name = "name")
    private String name;

    @Column(unique = false, name = "type")
    private String type;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] data;



    public PatientImage(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
