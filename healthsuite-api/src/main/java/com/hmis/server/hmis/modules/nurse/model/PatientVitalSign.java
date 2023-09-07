package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_patient_vital_sign_data")
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
@ToString
public class PatientVitalSign extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "patient_detail_id", nullable = false)
	private PatientDetail patient;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User capturedBy;

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department captureFromLocation;

	@Column(name = "weight")
	private Double weight;

	@Column(name = "height")
	private Double height;

	@Column(name = "body_mass")
	private Double bodyMassIndex;

	@Column(name = "temperature")
	private Double temperature;

	@Column(name = "body_surface_area")
	private Double bodySurfaceArea;

	@Column(name = "respiratory_area")
	private Double respiratoryRate;

	@Column(name = "pulse_rate")
	private Double pulseRate;

	@Column(name = "systolic_bp")
	private Double systolicBp;

	@Column(name = "diastolic_bp")
	private Double diastolicBp;

	@Column(name = "random_blood_sugar")
	private Double randomBloodSugar;

	@Column(name = "fast_blood_sugar")
	private Double fastBloodSugar;

	@Column(name = "oxygen_saturation")
	private Double oxygenSaturation;

	@Column(name = "pain_score")
	private Double painScore;

	@Column(name = "urine_analysis")
	private Double urineAnalysis;

	@Column(name = "comment_remark")
	private String commentRemark;

	@OneToOne
	@JoinColumn(name = "assign_user_id")
	private User assignTo;

	@Column( name = "is_nurse_module" )
	private Boolean isNurseModule = false;

	@Column( name = "is_doctor_module" )
	private Boolean isDoctorModule = false;

	@Column(name = "date")
	private LocalDate date = LocalDate.now();

	@Column(name = "time")
	private LocalTime time = LocalTime.now();
}
