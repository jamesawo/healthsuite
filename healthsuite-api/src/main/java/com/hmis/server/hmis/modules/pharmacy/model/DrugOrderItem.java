package com.hmis.server.hmis.modules.pharmacy.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "hmis_drug_order_item_data")
@NoArgsConstructor
public class DrugOrderItem {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false, name = "drug_order_id")
	private DrugOrder drugOrder;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "rate", nullable = false)
	private Integer rate;

	@Column(name = "total_amount", nullable = false)
	private Double totalAmount;

	@OneToOne
	@JoinColumn(name = "drug_register_id", nullable = false)
	private DrugRegister drugRegister;
}
