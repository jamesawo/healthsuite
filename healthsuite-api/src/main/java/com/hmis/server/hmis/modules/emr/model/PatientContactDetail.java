package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.dto.NationalityDto;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Nationality;
import com.hmis.server.hmis.modules.emr.dto.PatientContactDetailDto;

import java.util.Optional;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_patient_contact_detail_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class PatientContactDetail extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String residentialAddress;
    @Column
    private String email;
    @Column
    private String phoneNumber;
    @OneToOne(fetch = FetchType.LAZY)
    private Nationality nationality;

    public PatientContactDetailDto transformToDTO() {
        PatientContactDetailDto contactDetailDto = new PatientContactDetailDto();
        if (this.id != null) {
            contactDetailDto.setId(Optional.of(this.id));
        }
        if (this.email != null) {
            contactDetailDto.setEmail(this.email);
        }
        if (this.residentialAddress != null) {
            contactDetailDto.setResidentialAddress(this.residentialAddress);
        }
        if (this.phoneNumber != null) {
            contactDetailDto.setPhoneNumber(this.phoneNumber);
        }
        if (this.nationality != null) {
            contactDetailDto.setNationalityId(this.nationality.getId());
            contactDetailDto.setNationality(new NationalityDto(this.nationality.getId(), this.nationality.getName()));
        }
        return contactDetailDto;
    }
}
