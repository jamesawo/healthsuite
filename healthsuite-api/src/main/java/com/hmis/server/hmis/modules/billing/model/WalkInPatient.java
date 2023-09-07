package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.dto.GenderDto;
import com.hmis.server.hmis.common.common.model.Gender;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Entity
@Table( name = "hmis_walk_in_patient_data")
@NoArgsConstructor
public class WalkInPatient {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column
	private String firstName;

	@NotNull
	@Column
	private String lastName;

	@NotNull
	@Column
	private String otherName;

	@NotNull
	@Column
	private String address;

	@Column
	@NotNull
	private String phone;

	@Column
	@NotNull
	private String age;

	@OneToOne()
	@JoinColumn
	private Gender gender;

	public WalkInPatient(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName + " " + this.otherName;
	}

	public PatientDetailDto mapToPatientDto() {
		PatientDetailDto patientDetailDto = new PatientDetailDto();
		patientDetailDto.setPatientFirstName(this.getFirstName());
		patientDetailDto.setPatientLastName(this.getLastName());
		patientDetailDto.setPatientAge(this.getAge());
		patientDetailDto.setGenderDto(new GenderDto(Optional.ofNullable(this.gender.getId()), Optional.ofNullable(this.gender.getName())));
		return patientDetailDto;
	}

}
