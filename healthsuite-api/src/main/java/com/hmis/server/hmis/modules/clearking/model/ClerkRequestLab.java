package com.hmis.server.hmis.modules.clearking.model;

import com.fasterxml.jackson.annotation.*;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_clerk_request_lab_data")
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ClerkRequestLab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "other_information")
    private String otherInformation;

    @OneToMany(mappedBy = "labRequest")
    @Column
    private List<ClerkRequestLabItems> labItems;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "patient_detail_id")
    private PatientDetail patientDetail;

    @JsonIgnore
    @OneToOne(mappedBy = "labRequest")
    private ClerkDoctorRequest doctorRequest;

    @Column(name = "lab_request_code")
    private String code;

    @Transient
    private UserDto physician;

    @Transient
    private LocalDate date;

    @Transient
    private Long doctorRequestId;
}
