package com.hmis.server.hmis.modules.lab.model;

import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.WalkInPatient;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestLab;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_lab_test_request_data")
@NoArgsConstructor
@ToString
public class LabTestRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "lab_number")
    private String labNumber;

    @Column(name = "code")
    private String code;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "time")
    private LocalTime time = LocalTime.now();

    @OneToOne
    @JoinColumn(name = "patient_id")
    private PatientDetail patient;

    @OneToOne
    @JoinColumn(name = "walk_in_patient_id")
    private WalkInPatient walkInPatient;

    @OneToOne
    @JoinColumn(name = "request_lab_id") // lab request from doctor's clerking
    private ClerkRequestLab requestLab;

    @Column(name = "is_doctor_request")
    private Boolean isDoctorRequest = false;

    @OneToOne
    @JoinColumn(name = "patient_bill_id")
    private PatientBill bill;

    @OneToMany(mappedBy = "labTestRequest")
    private List<LabTestRequestItem> testItems;

    @Column(name = "is_specimen_collected")
    private Boolean isSpecimenCollected = false;

    public LabTestRequest(Long id) {
        this.id = id;
    }
}
