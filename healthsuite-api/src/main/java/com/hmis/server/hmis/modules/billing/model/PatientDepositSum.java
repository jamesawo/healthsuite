package com.hmis.server.hmis.modules.billing.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

/*
	Patient deposit sum merges all deposit amount from
	PatientDepositLog into one record
*/

@Data
@Entity
@Table( name = "hmis_patient_deposit_sum_data")
@NoArgsConstructor
@EqualsAndHashCode( callSuper=true) @ToString
public class PatientDepositSum extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonManagedReference
	@OneToOne( cascade = CascadeType.ALL )
	@JoinColumn(name = "patient_detail_id", nullable = false, unique = true)
	private PatientDetail patientDetail;

	@Column(name = "total_deposit_amount", nullable = false)
	@ColumnDefault("0.00")
	private Double totalDepositAmount;


	public PatientDepositSum(PatientDetail patientDetail, Double totalDepositAmount) {
		this.patientDetail = patientDetail;
		this.totalDepositAmount = totalDepositAmount;
	}

	public PatientDepositSum(PatientDetail patientDetail) {
		this.patientDetail = patientDetail;
	}
}
