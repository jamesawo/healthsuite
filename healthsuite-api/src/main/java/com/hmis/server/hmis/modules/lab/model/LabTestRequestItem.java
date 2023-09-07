package com.hmis.server.hmis.modules.lab.model;

import com.hmis.server.hmis.common.common.model.LabSpecimen;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_lab_test_request_item_data" )
@NoArgsConstructor
public class LabTestRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_service_bill_item_id")
    private PatientServiceBillItem billItem;

    @ManyToOne
    @JoinColumn(name = "lab_test_request_id")
    private LabTestRequest labTestRequest;

    @OneToOne
    @JoinColumn(name = "lab_specimen_id")
    private LabSpecimen specimen;

    @Column(name = "sample_status")
    private Boolean sampleStatus;

    @Column(name = "acknowledgment")
    private String acknowledgement;

    @Column(name = "comment")
    private String comment;

    @Column(name = "film_label")
    private String filmLabel;

    public LabTestRequestItem(Long id) {
        this.id = id;
    }
}
