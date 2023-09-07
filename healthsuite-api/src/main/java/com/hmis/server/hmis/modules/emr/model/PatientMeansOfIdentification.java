package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.MeansOfIdentification;
import com.hmis.server.hmis.modules.emr.dto.PatientMeansOfIdentificationDto;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_patient_means_of_identification_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@ToString
public class PatientMeansOfIdentification extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String identificationNumber;

    @Column
    private LocalDate expiryDate;

    @OneToOne
    @JoinColumn(name = "identification_type_id")
    private MeansOfIdentification identificationTypeId;


    public PatientMeansOfIdentificationDto transformToDTO() {
        PatientMeansOfIdentificationDto dto = new PatientMeansOfIdentificationDto();
        if (this.id != null)
            dto.setId(java.util.Optional.of(this.id));
        if (this.identificationNumber != null)
            dto.setIdentificationNumber(this.identificationNumber);
        if (this.identificationTypeId != null)
            dto.setIdentificationType(this.identificationTypeId.getId());
        if (this.expiryDate != null)
            dto.setExpiryDate(
                    new DateDto(this.expiryDate.getYear(),
                            this.expiryDate.getMonthValue(),
                            this.expiryDate.getDayOfMonth()
                    ));
        return dto;
    }
}
