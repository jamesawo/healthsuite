package com.hmis.server.hmis.modules.billing.model;

import com.hmis.server.hmis.common.common.model.ProductService;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_patient_service_bill_item_data" )
@NoArgsConstructor
public class PatientServiceBillItem {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Double quantity;

	@Column(nullable = false)
	private Double price;

	@Column(nullable = false)
	private Double gross = 0.00;

	@Column
	private Boolean payCash;

	@Column
	private Double nhisPrice = 0.00;

	@Column
	private Double nhisPercent = 0.00;

	@Column(nullable = false)
	private Double netAmount = 0.00;

	@Column(name = "discount_amount", columnDefinition = "double precision default '0.00'")
	private Double discountAmount = 0.00;

	@OneToOne
	@JoinColumn(nullable = false, name = "product_service_id")
	private ProductService productService;

	@ManyToOne()
	@JoinColumn( name = "patient_service_bill_id", nullable = false )
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
