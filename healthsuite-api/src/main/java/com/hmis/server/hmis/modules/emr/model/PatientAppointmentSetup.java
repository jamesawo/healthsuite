package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hmis_patient_appointment_setup_data")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class PatientAppointmentSetup extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime dateTime = LocalDateTime.now();

	@OneToOne
	@JoinColumn(unique = true, name = "consultant_user", nullable = false)
	private User consultant;

	@OneToOne
	@JoinColumn(name = "speciality")
	private SpecialityUnit specialityUnit;

	@Column(name = "staff_limit", nullable = false)
	private int staffLimit;


}
