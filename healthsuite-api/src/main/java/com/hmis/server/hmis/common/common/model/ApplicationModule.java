package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_application_module_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class ApplicationModule extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String code;

    public ApplicationModule(Long id){
        this.id = id;
    }



}
