package com.hmis.server.hmis.modules.nurse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_nurse_ob_delivery_instruction_data")
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class NurseObDeliveryInstruction {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promontory", columnDefinition="TEXT")
    private String promontory;

    @Column(name = "sacrum", columnDefinition="TEXT")
    private String sacrum;

    @Column(name = "sidewalls", columnDefinition="TEXT")
    private String sidewalls;

    @Column(name = "i_schial_sp", columnDefinition="TEXT")
    private String iSchialSp;

    @Column(name = "sub_pub", columnDefinition="TEXT")
    private String subPub;

    @Column(name = "coccyx", columnDefinition="TEXT")
    private String coccyx;

    @Column(name = "comments", columnDefinition="TEXT")
    private String comments;

    @Column(name = "delivery_instructions", columnDefinition="TEXT")
    private String deliveryInstructions;
}
