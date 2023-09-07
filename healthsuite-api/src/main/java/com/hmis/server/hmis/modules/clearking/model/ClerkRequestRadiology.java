package com.hmis.server.hmis.modules.clearking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_clerk_request_radiology_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ClerkRequestRadiology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "previous_image_details")
    private String previousImagingDetails;

    @Column(name = "previous_operation_details")
    private String previousOperationDetails;

    @Column(name = "patient_current_status")
    private String patientCurrentStatus;

    @Column(name = "other_information")
    private String otherInformation;

    @OneToMany(mappedBy = "radiologyRequest")
    @Column
    private List<ClerkRequestRadiologyItems> radiologyItems;

    @JsonIgnore
    @OneToOne()
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patientDetail;

    @Column(name = "lab_request_code")
    private String code;

    @JsonIgnore
    @OneToOne(mappedBy = "radiologyRequest")
    private ClerkDoctorRequest doctorRequest;

    @Transient
    private UserDto physician;

    @Transient
    private LocalDate date;

    @Transient
    private Long doctorRequestId;

}
