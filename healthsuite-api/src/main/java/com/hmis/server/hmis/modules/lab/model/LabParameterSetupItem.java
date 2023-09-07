package com.hmis.server.hmis.modules.lab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_lab_parameter_setup_item_data")
@SQLDelete(sql = "UPDATE hmis_lab_parameter_setup_item_data SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabParameterSetupItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lab_parameter_id")
    private LabParameter parameter;

    @Column(name = "parameterHierarchy")
    private Integer parameterHierarchy;

    @ManyToOne
    @JoinColumn(name = "lab_parameter_setup_id")
    private LabParameterSetup labParameterSetup;

    @Column
    private boolean deleted = Boolean.FALSE;

    public LabParameterSetupItem(Long id) {
        this.id = id;
    }
}
