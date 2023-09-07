package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.model.SchemePlan;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/*
	SchemeBill keep record of bills generated for scheme patient
	Used to generate report of how much scheme provider owes hospital and what services was provider to scheme patient
 */
@Data
@Entity
@Table( name = "hmis_scheme_bill_data" )
@NoArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class SchemeBill extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@OneToOne
	@JoinColumn( name = "patient_bill_id", nullable = false )
	private PatientBill patientBill;

	@Column( name = "amount", nullable = false )
	private double amount;

	@Column( name = "nhis_number" )
	private String nhisNumber;

	@Column( name = "diagnosis" )
	private String diagnosis;

	@Column( name = "approval_code" )
	private String approvalCode;

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@Column( name = "time" )
	private LocalTime time = LocalTime.now();

	@OneToOne
	@JoinColumn( nullable = false )
	private Scheme scheme;

	@OneToOne()
	@JoinColumn( name = "scheme_Plan_id" )
	private SchemePlan schemePlan;

	@OneToOne
	@JoinColumn( name = "patient_detail_id" )
	private PatientDetail patientDetail;

	@Column( name = "is_out_patient" )
	private Boolean isOutPatient;


}
