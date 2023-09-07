package com.hmis.server.hmis.modules.lab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "hmis_lab_parameter_range_setup_item_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabParameterRangeSetupItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;

    @Column(name = "lower_limit")
    @ColumnDefault("0")
    private Double lowerLimit;

    @Column(name = "upper_limit")
    @ColumnDefault("0")
    private Double upperLimit;

    @ManyToOne
    @JoinColumn(name = "range_setup_id")
    private LabParameterRangeSetup rangeSetup;

    public LabParameterRangeSetupItem(Long id) {
        this.id = id;
    }
}
