package com.hmis.server.hmis.modules.settings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_global_Settings_data")
@NoArgsConstructor @AllArgsConstructor
public class GlobalSettings  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String key;

    @Column
    private String value;

    @Column
    private String section;

    public GlobalSettings(String key, String value, String section) {
        this.key = key;
        this.value = value;
        this.section = section;
    }
}
