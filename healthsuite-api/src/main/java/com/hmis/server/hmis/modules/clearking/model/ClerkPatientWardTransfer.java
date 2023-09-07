package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_clerk_patient_ward_transfer_data")
@NoArgsConstructor
@ToString
public class ClerkPatientWardTransfer {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patient;

    @OneToOne()
    @JoinColumn(name = "prev_ward_id")
    private Ward preWard;

    @Column(name = "prev_bed_code")
    private String prevBedCode;

    @OneToOne()
    @JoinColumn(name = "new_ward_id")
    private Ward newWard;

    @Column(name = "new_bed_code")
    private String newBedCode;

    @OneToOne
    @JoinColumn(name = "transferred_by_user_id")
    private User transferredBy;

    @Column(name = "transfer_note")
    private String transferNote;

    @OneToOne
    @JoinColumn(name = "pre_consultant_user_id")
    private User prevConsultant;

    @OneToOne
    @JoinColumn(name = "new_consultant_user_id")
    private User newConsultant;

    @Column(name = "transfer_date")
    private LocalDate transferDate = LocalDate.now();

    @Column(name = "transfer_time")
    private LocalTime transferTime =  LocalTime.now();

    @OneToOne
    @JoinColumn(name = "location_dep_id")
    private Department location;

}
