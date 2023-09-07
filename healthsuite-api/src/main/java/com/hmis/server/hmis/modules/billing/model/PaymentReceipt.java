package com.hmis.server.hmis.modules.billing.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hmis.server.hmis.modules.billing.dto.ReceiptStatusEnum;
import com.hmis.server.hmis.modules.billing.dto.ReceiptTypeEnum;
import com.hmis.server.hmis.modules.billing.service.PatientPaymentSearchService;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

// store receipt information, so it can be retrieved later
@Data
@Entity
@Table( name = "hmis_payment_receipt_data")
@NoArgsConstructor
@ToString
public class PaymentReceipt {
	/* payment receipt is created before generating receipt file (ie, after making payment, a receipt is generated
	 and printed for the patient, so payment receipt is saved to storage right before generating the  receipt file.*/
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column( name = "receipt_number", nullable = false, unique = true)
	private String receiptNumber;

	@Column( name = "receipt_type_enum", nullable = false)
	private ReceiptTypeEnum receiptTypeEnum;

	@Column( name = "receipt_status_enum", nullable = false )
	private ReceiptStatusEnum receiptStatusEnum;

	@OneToOne
	@JoinColumn( name = "patient_detail_id" )
	private PatientDetail patientDetail;

	@OneToOne
	@JoinColumn( name = "walkin_patient_id" )
	private WalkInPatient walkInPatient;

	@Column(name = "transact_date", nullable = false)
	private LocalDate localDate  = LocalDate.now();

	@Column(name = "transact_time", nullable = false)
	private LocalTime localTime = LocalTime.now();

	@Column(name = "shift_number")
	private String shiftNumber;

	@Column(name = "is_used", nullable = false)
	@ColumnDefault("false")
	private Boolean isUsed = false;

	@Column(name = "is_touched", nullable = false)
	@ColumnDefault("false")
	private Boolean isTouched = false;

	@OneToOne()
	@JoinColumn(name = "patient_payment_id")
	private PatientPayment payment;

}
