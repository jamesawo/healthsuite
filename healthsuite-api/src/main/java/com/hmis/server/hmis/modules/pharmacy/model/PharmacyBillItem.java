package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_pharmacy_bill_item_data" )
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
public class PharmacyBillItem extends Auditable< String > {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column( name = "is_credit_bill" )
	private Boolean isCreditBill; // inactive

	@Column( name = "is_pay_cash" )
	private Boolean isPayCash;

	@Column( name = "nhis_price" )
	@ColumnDefault( "0.00" )
	private Double nhisPrice;

	@Column( name = "nhis_percent" )
	@ColumnDefault( "0.00" )
	private Double nhisPercent;

	@Column( name = "dosage" )
	private int dosage;

	@Column( name = "frequency" )
	private String frequency;

	@Column( name = "days" )
	private int days;

	@Column( name = "available_quantity" )
	private int availableQuantity;

	@Column( nullable = false )
	private int quantity;

	@Column( nullable = false, name = "price_amount" )
	private Double priceAmount;

	@Column( nullable = false, name = "net_amount" )
	private Double netAmount;

	@Column( nullable = false, name = "gross_amount" )
	private Double grossAmount;

	@Column( name = "discount_amount" )
	private Double discountAmount;

	@OneToOne
	@JoinColumn( nullable = false, name = "drug_register_id" )
	private DrugRegister drugRegister;

	@ManyToOne()
	@JoinColumn( name = "patient_bill_id", nullable = false )
	private PatientBill patientBill;

	@Column(name = "is_adjusted")
	@ColumnDefault("false")
	private Boolean isAdjusted;

	@Column(name = "prev_qty")
	@ColumnDefault("0.00")
	private Double prevQty;

	@Column(name = "is_cancelled")
	@ColumnDefault("false")
	private Boolean isCancelled;

}
