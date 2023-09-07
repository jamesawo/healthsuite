package com.hmis.server.hmis.modules.clearking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.ProductService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_request_lab_items_data")
@NoArgsConstructor
@ToString
public class ClerkRequestLabItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_service_id")
    private ProductService service;

    @Column(name = "comment")
    private String comment;

    @Column(name = "specimen")
    private String specimen;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "request_lab_id")
    private ClerkRequestLab labRequest;
}
