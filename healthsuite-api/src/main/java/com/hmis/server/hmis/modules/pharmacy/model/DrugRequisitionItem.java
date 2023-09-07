package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "hmis_drug_requisition_item_data" )
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
public class DrugRequisitionItem extends Auditable< String > {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "drug_register_id", nullable = false)
	private DrugRegister drugRegister;

	@Column(name = "requesting_quantity", nullable = false)
	private Integer requestingQuantity;

	@Column(name = "unit_of_issue")
	private Integer unitOfIssue;

	@Column(name = "issuing_outlet_balance")
	private Integer issuingOutletBalance;

	@ManyToOne
	@JoinColumn(name = "drug_requisition_id", nullable = false)
	private DrugRequisition drugRequisition;

	@Column( name = "issuing_quantity" )
	private Integer issuingQuantity = 0;

}
