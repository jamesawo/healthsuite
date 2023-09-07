package com.hmis.server.hmis.modules.lab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.LabSpecimen;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_lab_parameter_range_setup_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabParameterRangeSetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // use both test and parameter item to get test range
    @OneToOne
    @JoinColumn(name = "test_service_id")
    private ProductService test;

    @OneToOne
    @JoinColumn(name = "lab_param_setup_item_id")
    private LabParameterSetupItem labParameterSetupItem;

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

    @OneToMany(mappedBy = "rangeSetup")
    private List<LabParameterRangeSetupItem> labParameterRangeSetupItems;

}
