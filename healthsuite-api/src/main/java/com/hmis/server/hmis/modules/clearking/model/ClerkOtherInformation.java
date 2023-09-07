package com.hmis.server.hmis.modules.clearking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_clerk_other_information_data")
@NoArgsConstructor
@ToString
public class ClerkOtherInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "other_information")
    private String otherInformation;

    public ClerkOtherInformation(String otherInformation){
        this.otherInformation = otherInformation;
    }
}
