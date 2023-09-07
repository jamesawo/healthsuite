package com.hmis.server.hmis.common.common.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_department_category_data")
@NoArgsConstructor
public class DepartmentCategory  {

    //department category is seeded on app startup time.
    //client does not create or modify this.
    //to interact with department category use department category enum or findByName

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column
    private String code;

    @OneToMany(mappedBy = "departmentCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Department> departments;

    public DepartmentCategory(Long id){
        this.id = id;
    }

    public DepartmentCategory(String name) {
        this.name = name;
    }

    public DepartmentCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
