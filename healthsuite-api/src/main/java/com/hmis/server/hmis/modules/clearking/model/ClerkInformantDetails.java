package com.hmis.server.hmis.modules.clearking.model;

import com.hmis.server.hmis.common.common.model.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_informant_details_data")
@NoArgsConstructor
@ToString
public class ClerkInformantDetails {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "informant_name")
    private String informantName;

    @Column(name = "informant_phone_number")
    private String informantPhoneNumber;

    @OneToOne
    @JoinColumn(name = "informant_relationship_id")
    private Relationship informantRelationship;
}
