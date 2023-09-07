package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Relationship;
import com.hmis.server.hmis.modules.emr.dto.PatientNOKDetailDto;
import java.util.Optional;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_patient_nok_detail_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class PatientNOKDetail extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    private Relationship relationship;

    @Column
    private String phone;

    @Column
    private String address;


    public PatientNOKDetailDto transformToDTO(){
        PatientNOKDetailDto nokDetailDto = new PatientNOKDetailDto();
        if (this.id != null)
            nokDetailDto.setId(Optional.of(this.id));
        if (this.name != null)
            nokDetailDto.setName(this.name);
        if (this.phone != null)
            nokDetailDto.setPhone(this.phone);
        if (this.address != null)
            nokDetailDto.setAddress(this.address);
        if (this.relationship != null && this.relationship.getId() != null)
            nokDetailDto.setRelationshipId(this.relationship.getId());
        return nokDetailDto;
    }

}


