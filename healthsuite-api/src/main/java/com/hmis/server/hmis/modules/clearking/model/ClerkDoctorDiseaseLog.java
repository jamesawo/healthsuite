package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_doctor_disease_log_data")
@NoArgsConstructor
@ToString
public class ClerkDoctorDiseaseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String code;

    public ClerkDoctorDiseaseLog(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
