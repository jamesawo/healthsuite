package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_location_map_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class LocationMap  extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private ApplicationModule applicationModule;

    @OneToOne
    private DepartmentCategory departmentCategory;

}
