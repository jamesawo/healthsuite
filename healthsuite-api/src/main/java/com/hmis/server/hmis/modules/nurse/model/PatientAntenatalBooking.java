package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table( name = "hmis_patient_antenatal_booking_data")
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
@ToString
public class PatientAntenatalBooking extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "patient_detail_id", nullable = false, unique = true)
	private PatientDetail patient;

	@OneToOne
	@JoinColumn(name = "booked_by_user_id", nullable = false)
	private User bookedBy;

	@OneToOne
	@JoinColumn(name = "capture_from_department_id", nullable = false)
	private Department captureFromLocation;

	@OneToOne
	@JoinColumn(name = "consultant_user_id")
	private User caseConsultant;

	@OneToOne
	@JoinColumn(name = "speciality_unit_id")
	private SpecialityUnit specialityUnit;

	@OneToOne
	@JoinColumn(name = "clinic_department_id")
	private Department clinic;

	@Column(name = "spouse_name")
	private String spouseName;

	@Column(name = "spouse_phone")
	private String spousePhone;

	@Column(name = "spouse_occupation")
	private String spouseOccupation;

	@Column(name = "spouse_employer")
	private String spouseEmployer;

	@Column(name = "date")
	private LocalDate date;

	@Column(name= "time")
	private LocalTime time;

}
