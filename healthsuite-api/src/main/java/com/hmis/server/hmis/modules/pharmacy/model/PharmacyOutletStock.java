package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table( name = "hmis_pharmacy_outlet_stock_data")
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
@ToString
public class PharmacyOutletStock extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "drug_register_id", nullable = false)
	private DrugRegister drugRegister;

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

	@Column(name = "balance")
	private Integer balance;
}
