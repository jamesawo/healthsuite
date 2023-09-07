package com.hmis.server.hmis.modules.clearking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_clerk_request_drug_item_data")
@NoArgsConstructor
@ToString
public class ClerkRequestDrugItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "drug_register_id")
    private DrugRegister drugRegister;

    @Column(name = "dosage")
    private Integer dosage;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "days")
    private Integer days;

    @Column(name = "admin_route")
    private String adminRoute;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "request_drug_id")
    private ClerkRequestDrug requestDrug;

    @Column(name = "nurse_comment")
    private String nurseComment;

    @Column(name = "is_administered")
    private Boolean isAdministered = false;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "administered_by_user_id")
    private User administeredBy;

    @Transient
    @JsonIgnore
    private String drugLabel;
    @Transient
    private Long userId;
    @Transient
    private Long locationId;
    @Transient
    private Long patientId;

}
