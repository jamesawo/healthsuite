package com.hmis.server.hmis.modules.clearking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hmis.server.hmis.common.common.model.ProductService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hmis_clerk_request_radiology_items_data")
@NoArgsConstructor
@ToString
public class ClerkRequestRadiologyItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "service_id")
    private ProductService service;

    @Column(name = "examination_required")
    private String examinationRequired;

    @Column(name = "comment")
    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "request_radiology_id")
    private ClerkRequestRadiology radiologyRequest;
}
