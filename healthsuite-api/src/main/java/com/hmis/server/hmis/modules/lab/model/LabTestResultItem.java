package com.hmis.server.hmis.modules.lab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

/* Lab test result keeps the value of the patient test
* use the range setup item to get the ranges and
* use test request item to get the parent lab test request */
@Data
@Entity
@Table( name = "hmis_lab_test_result_item_data")
@NoArgsConstructor
@ToString
public class LabTestResultItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_value")
    private Double testValue;

    @ManyToOne
    @JoinColumn(name = "lab_param_range_setup_id")
    private LabParameterRangeSetupItem rangeSetupItem; // use range to get the test parameter data

    @ManyToOne
    @JoinColumn(name = "lab_test_result_id")
    private LabTestResult labTestResult;
}
