package com.hmis.server.hmis.modules.billing.model;


import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkDoctorRequest;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_patient_bill_data" )
@NoArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class PatientBill extends Auditable<String> {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	/* bill type SERVICE, DRUG, DEPOSIT; */
	@Column( name = "bill_type_for", nullable = false )
	private PaymentTypeForEnum billTypeEnum;

	@OneToOne( cascade = CascadeType.ALL )
	@JoinColumn( name = "patient_detail_id" )
	private PatientDetail patient;

	@Column( name = "patient_category" )
	private PatientCategoryEnum patientCategoryEnum;

	@Column( name = "bill_search_type" )
	private String billSearchType;

	@Column( name = "bill_patient_type" )  //BillPatientTypeEnum => WALK_IN OR REGISTERED
	private String billPatientType;

	@Column( nullable = false, name = "gross_total" )
	private Double grossTotal;

	@Column( nullable = false, name = "net_total" )
	private Double netTotal;

	@Column( nullable = false, name = "discount_total" )
	private Double discountTotal;

	@OneToOne( cascade = CascadeType.ALL )
	@JoinColumn( nullable = false, name = "cost_by_user_id" )
	private User costBy;

	@OneToOne
	@JoinColumn( nullable = false, name = "location_id" )
	private Department location;

	@Column( nullable = false, name = "cost_date" )
	private LocalDateTime costDate = LocalDateTime.now();

	@OneToOne
	@JoinColumn( name = "walkin_patient_id" )
	private WalkInPatient walkInPatient;

	@Column( name = "is_credit" )
	private Boolean isCredit;

	@Column( name = "invoice_number", unique = true, nullable = false )
	private String invoiceNumber;

	@Column( name = "is_paid", nullable = false )
	@ColumnDefault( "false" )
	private Boolean isPaid = false;

	@Column( name = "receipt_number" )
	private String receiptNumber;

	// if paymentTypeFor is service then bill will contain service bill items
	@OneToMany( mappedBy = "patientBill", fetch = FetchType.LAZY )
	@Column()
	private List<PatientServiceBillItem> patientServiceBillItems;

	// if paymentTypeFor is drug bill then bill items will contain drug bill items
	@OneToMany( mappedBy = "patientBill", fetch = FetchType.LAZY )
	@Column()
	private List<PharmacyBillItem> pharmacyBillItems;

	@Column( name = "is_doctor_request" )
	private Boolean isDoctorRequest = false;

	@OneToOne()
	@JoinColumn( name = "doctor_request_id" )
	private ClerkDoctorRequest doctorRequest;

	@Column( name = "is_on_admission", nullable = false )
	@ColumnDefault( "false" )
	private Boolean isOnAdmission = false;

	@ManyToOne()
	@JoinColumn( name = "patient_admission_id" )
	private PatientAdmission patientAdmission;

	@Column( name = "is_adjusted" )
	@ColumnDefault( "false" )
	private Boolean isAdjusted = false;

	@OneToOne
	@JoinColumn( name = "adjusted_bill_id" )
	private PatientAdjustedBill adjustedBill;

	@OneToOne( mappedBy = "patientBill", optional = true )
	private PatientPayment payment;

	public PatientBill( Long id ) {
		this.id = id;
	}

	@PostPersist
	public void postSave() {

	}

	public boolean isServiceBill() {
		return this.billTypeEnum.equals( PaymentTypeForEnum.SERVICE );
	}

	public boolean hasServiceBillItems() {
		return isServiceBill() && !this.patientServiceBillItems.isEmpty();
	}

	public boolean isDrugBill() {
		return this.billTypeEnum.equals( PaymentTypeForEnum.DRUG );
	}

}
