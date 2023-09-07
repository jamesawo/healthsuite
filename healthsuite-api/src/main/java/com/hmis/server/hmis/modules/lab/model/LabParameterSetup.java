package com.hmis.server.hmis.modules.lab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.LabSpecimen;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.lab.dto.LabDepartmentTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_lab_parameter_setup_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabParameterSetup  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "test_service_id")
    private ProductService test;

    @OneToOne
    @JoinColumn(name = "specimen_id")
    private LabSpecimen specimen;

    @Column(name = "specimen_color")
    private String specimenColor;

    @OneToOne
    @JoinColumn(name = "captured_by_user_id")
    private User capturedBy;

    @OneToOne
    @JoinColumn(name = "captured_from_dep_id")
    private Department capturedFrom;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "time")
    private LocalTime time = LocalTime.now();

    @OneToMany(mappedBy = "labParameterSetup")
    private List<LabParameterSetupItem> parameterSetupItems;

    @Column(name = "is_parasitology_test")
    private Boolean isParasitologTest = false;

    @Column(name = "is_require_pathologist")
    private Boolean isRequirePathologist = false;

    @Column(name = "is_special_test")
    private Boolean isSpecialTest = false;

    @Column(name = "is_immuno_test")
    private Boolean isImmunoTest = false;

    @Column(name = "is_bone_marrow_test")
    private Boolean isBoneMarrowTest = false;

    @Column(name = "is_histopathology_sfa_test")
    private Boolean isHistopathologySFATest = false;

    @Column(name = "department_type_enum")
    private LabDepartmentTypeEnum departmentTypeEnum;

}
