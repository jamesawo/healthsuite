package com.hmis.server.hmis.modules.pharmacy.model;

import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_pharmacy_received_good_item_data")
@NoArgsConstructor
@ToString
public class PharmacyReceivedGoodsItem {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "quantity_ordered", nullable = false)
	private Integer quantityOrdered;

	@Column(name = "quantity_received")
	private Integer quantityReceived;

	@Column(name = "quantity_supplied")
	private Integer quantitySupplied;

	@Column( name ="rate")
	private Integer rate;

	@Column(name = "unit_of_issue")
	private Integer unitOfIssue;

	@Column(name = "total_cost", nullable =  false)
	@ColumnDefault("0.00")
	private Double totalCost;

	@Column(name = "batch_number")
	private String batchNumber;

	@Column(name = "expiry_date")
	private LocalDate expiryDate;

	@OneToOne
	@JoinColumn(name = "drug_register_id", nullable = false)
	private DrugRegister drugRegister;

	@ManyToOne
	@JoinColumn(nullable = false, name = "pharmacy_received_goods_id")
	private PharmacyReceivedGoods pharmacyReceivedGoods;
}
