package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Bed;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table(name = "hmis_patient_admission_data")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class PatientAdmission extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, name = "code")
	private String code;

	@Column(name = "admission_status")
	@ColumnDefault(value = "0")
	private PatientAdmissionStatusEnum admissionStatus;

	@OneToOne
	@JoinColumn(name = "consultant_user_id")
	private User consultant;

	@OneToOne
	@JoinColumn(name = "patient_id")
	private PatientDetail patient;

	@OneToOne
	@JoinColumn(name="ward_id")
	private Ward ward;

	@OneToOne
	@JoinColumn(name = "bed_id")
	private Bed bed;

	@OneToOne
	@JoinColumn(name = "admitted_by_user_id")
	private User admittedBy;

	@OneToOne
	@JoinColumn(name = "discharged_by_user_id")
	private User dischargedBy;

	@Column
	private String remark;

	@Column(name = "discharged_date")
	private LocalDate dischargedDate;

	@Column(name = "discharged_time")
	private LocalTime dischargedTime;

	@Column(name = "admitted_date")
	private LocalDate admittedDate;

	@Column(name = "admitted_time")
	private LocalTime admittedTime;

	@OneToOne
	@JoinColumn(name = "location_dep_id")
	private Department admittedLocation;

	@OneToMany(mappedBy = "patientAdmission")
	private List<PatientBill> bills;

	@OneToMany(mappedBy = "admission")
	private List<PatientPayment> payments;

	@Column(name = "final_diagnosis")
	private String finalDiagnosis;

	@Column(name = "other_comment")
	private String otherComment;

	@Column(name = "discharge_status")
	private String dischargeStatus;

	@OneToOne
	@JoinColumn(name = "discharged_from_loc_id")
	private Department dischargeLocation;

	@OneToOne
	@JoinColumn(name = "discharged_consultant_user_id")
	private User dischargedConsultant;

}

