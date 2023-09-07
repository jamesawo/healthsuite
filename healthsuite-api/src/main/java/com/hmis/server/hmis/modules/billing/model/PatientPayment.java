package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.PaymentMethod;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.dto.BillPaymentOptionTypeEnum;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

@Data
@Entity
@Table( name = "hmis_patient_payment_data" )
@ToString
@NoArgsConstructor
public class PatientPayment {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "bill_payment_type", nullable = false)
	private BillPaymentOptionTypeEnum billPaymentOptionTypeEnum;

	@Column( name = "receipt_number", nullable = false)
	private String receiptNumber;

	@Column( name = "gross_total", nullable = false )
	@ColumnDefault( "0.00" )
	private Double grossTotal;

	@Column( name = "discount_total", nullable = false )
	@ColumnDefault( "0.00" )
	private Double discountTotal = 0.00;

	@Column( name = "waived_total", nullable = false )
	@ColumnDefault( "0.00" )
	private Double waivedTotal = 0.00;

	@Column( name = "deposit_allocated_total", nullable = false )
	@ColumnDefault( "0.00" )
	private Double depositAllocatedTotal;

	@Column( name = "net_total", nullable = false )
	@ColumnDefault( "0.00" )
	private Double netTotal;

	@Column( name = "date", nullable = false)
	private LocalDate date = LocalDate.now();

	@Column( name = "time", nullable = false)
	private LocalTime time = LocalTime.now();

	@OneToOne
	@JoinColumn(name = "patient_bill_id", unique = true)
	private PatientBill patientBill;

	@OneToOne()
	@JoinColumn( name = "location_department_id", nullable = false)
	private Department location;

	@OneToOne
	@JoinColumn( name = "cashier_user_id", nullable = false)
	private User cashier;

	@OneToOne
	@JoinColumn( name = "payment_method_id", nullable = false)
	private PaymentMethod paymentMethod;

	@Column(name = "payment_type_for", nullable = false)
	private String paymentTypeForEnum;

	@ManyToOne
	@JoinColumn(name = "cashier_shift_id")
	private CashierShift cashierShift;

	@OneToOne
	@JoinColumn(name = "deposit_log_id")
	private PatientDepositLog depositLog;

	@Column(name = "is_cancelled")
	@ColumnDefault(value = "false")
	private Boolean isCancelled = false;

	@OneToOne
	@JoinColumn(name = "cancelled_payment_id")
	private CancelledPayment cancelledPayment;

	@Column(name = "cancelled_net_amount")
	@ColumnDefault(value = "0.00")
	private Double cancelledNetAmount = 0.00;

	@ManyToOne
	@JoinColumn(name = "admission_id")
	private PatientAdmission admission;

	@OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
	private PaymentReceipt receipt;

}
