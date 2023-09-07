package com.hmis.server.hmis.modules.lab.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_lab_test_tracker_data" )
@NoArgsConstructor
public class LabTestTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_type")
    private String billType;

    @Column(name = "lab_test_code")
    private String labTestCode;

    @OneToOne
    @JoinColumn(name = "lab_test_request_id")
    private LabTestRequest labTestRequest;

    @OneToOne
    @JoinColumn(name = "lab_test_request_item_id")
    private LabTestRequestItem labTestRequestItem;


    @Column(name = "is_specimen_collected")
    private Boolean isSpecimenCollected = false;

    @OneToOne
    @JoinColumn(name = "specimen_collection_id")
    private LabSpecimenCollection specimenCollection;

    @Column(name = "is_specimen_ack")
    private Boolean isSpecimenAck  = false;

    @OneToOne
    @JoinColumn(name = "specimen_ack_id")
    private LabSpecimenAck specimenAck;

    @Column(name = "is_lab_test_prepared")
    private Boolean isLabTestPrepared  = false;

    @OneToOne
    @JoinColumn(name = "lab_test_preparation_id")
    private LabTestPreparation labTestPreparation;

    @Column(name = "is_lab_test_verified")
    private Boolean isLabTestVerified  = false;

    @OneToOne
    @JoinColumn(name = "lab_test_verification_id")
    private LabTestVerification labTestVerification;

    @Column(name = "is_pathologist_verified")
    private Boolean isPathologistVerified  = false;

    @OneToOne
    @JoinColumn(name = "pathologist_verification_id")
    private LabTestPathologistVerification pathologistVerification;

    public LabTestTracker(String labTestCode, LabTestRequest labTestRequest) {
        this.labTestCode = labTestCode;
        this.labTestRequest = labTestRequest;
    }
}
