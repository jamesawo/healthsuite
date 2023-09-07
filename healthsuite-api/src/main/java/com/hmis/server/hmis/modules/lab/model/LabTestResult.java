package com.hmis.server.hmis.modules.lab.model;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_lab_test_result_data")
@NoArgsConstructor
@ToString
public class LabTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @Column(name = "film_report")
    private String filmReport;

    @Column(name = "comment")
    private String comment;
     */

    @ManyToOne
    @JoinColumn(name = "lab_test_request_item_id")
    private LabTestRequestItem testRequestItem;

    @ManyToOne
    @JoinColumn(name = "lab_test_preparation_id")
    private LabTestPreparation testPreparation;

    @ManyToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patient;

    @OneToMany(mappedBy = "labTestResult")
    private List<LabTestResultItem> resultItems;
}
