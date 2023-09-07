package com.hmis.server.hmis.modules.nurse.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table( name = "hmis_nurse_ob_general_form_data")
@NoArgsConstructor
@ToString
public class NurseObGeneralForm {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "special_point", columnDefinition="TEXT")
    private String specialPoints;

    @Column(name = "special_attention", columnDefinition="TEXT")
    private String specialAttention;

    @Column(name = "lmp", columnDefinition="TEXT")
    private LocalDate lmp;

    @Column(name = "edd", columnDefinition="TEXT")
    private String edd;

    @Column(name = "ga", columnDefinition="TEXT")
    private String ga;
}
