package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "hmis_patient_appointment_data")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class PatientAppointment extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column( nullable = false )
	private LocalDate date;

	@ManyToOne
	@JoinColumn( name = "patient_id", nullable = false )
	private PatientDetail patientDetail;

	@ManyToOne
	@JoinColumn( name = "consultant_id", nullable = false )
	private User consultant;

	@OneToOne
	@JoinColumn(name = "speciality")
	private SpecialityUnit specialityUnit;

	@OneToOne
	@JoinColumn(name = "booked_by_user")
	private User bookedBy;

	@OneToOne
	@JoinColumn( name = "location" )
	private Department location;

	@Column( name = "appointment_status", nullable = false )
	private String appointmentStatus = PatientAppointmentStatusEnum.OPEN.label;

	@Column
	private String remarks;

	@Column
	private LocalTime time;

	@OneToOne
	@JoinColumn( name = "ward" )
	private Department ward;

}
