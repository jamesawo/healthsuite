package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/*
	Patient deposit history keeps track of
	What services/products or drugs payment a patient deposit amount is being charged/used for
 */
@Data
@Entity
@Table( name = "hmis_patient_deposit_history_data")
@NoArgsConstructor
@EqualsAndHashCode( callSuper=true)
public class PatientDepositHistory extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "before_charge_amount", nullable = false)
	@ColumnDefault("0.00")
	private Double beforeChargeAmount;

	@Column(name = "after_charge_amount")
	@ColumnDefault("0.00")
	private Double afterChargeAmount;

	@ManyToOne()
	@JoinColumn(name = "patient_bill_id", nullable = false)
	private PatientBill chargeForBill; //Patient bill record

	@Column(name = "date_time")
	private LocalDateTime localDateTime = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "patient_deposit_id", nullable = false)
	private PatientDepositSum patientDepositSum;

	@OneToOne
	@JoinColumn(name = "patient_detail_id", nullable = false)
	private PatientDetail patientDetail;
}
