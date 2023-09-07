package com.hmis.server.hmis.modules.billing.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.PaymentMethod;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

/*
	Patient Deposit Log keep tracks of
	all the deposit amount made by a patient
*/

@Data
@Entity
@Table( name = "hmis_patient_deposit_log_data")
@NoArgsConstructor @ToString
@EqualsAndHashCode( callSuper=true)
public class PatientDepositLog extends Auditable<String> {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn( name = "patient_detail_id", nullable = false)
	private PatientDetail patient;

	@Column(nullable = false, name = "date")
	private LocalDate date = LocalDate.now();

	@Column(name = "time", nullable = false)
	private LocalTime time = LocalTime.now();

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false) // location of the cashier
	private Department department;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false) //the cashier details
	private User user;

	@OneToOne
	@JoinColumn(name = "revenue_department_id", nullable = false)
	private RevenueDepartment revenueDepartment;

	@OneToOne
	@JoinColumn(name = "payment_method_id", nullable = false)
	private PaymentMethod paymentMethod;

	@Column(name = "pos_transact_ref")
	private String posTransactRef;

	@Column(name = "deposit_amount", nullable = false)
	private Double depositAmount;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "is_used", nullable = false)
	@ColumnDefault( "false")
	private Boolean isUsed = false;

	@OneToOne
	@JoinColumn(name = "patient_admission_id")
	private PatientAdmission patientAdmission;

	@ManyToOne
	@JoinColumn(name = "patient_deposit_sum_id")
	private PatientDepositSum patientDepositSum;

	@JsonManagedReference
	@OneToOne
	private PaymentReceipt paymentReceipt;

}
