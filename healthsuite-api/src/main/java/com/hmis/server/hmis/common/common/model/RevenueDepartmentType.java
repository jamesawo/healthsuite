package com.hmis.server.hmis.common.common.model;


import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hmis_revenue_department_type_data")
@NoArgsConstructor
public class RevenueDepartmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public RevenueDepartmentType(String name) {
        this.name = name;
    }
}
