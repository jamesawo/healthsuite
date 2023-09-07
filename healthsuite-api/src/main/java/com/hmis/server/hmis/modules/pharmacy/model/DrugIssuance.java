package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.pharmacy.dto.IssuanceTypeEnum;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table( name = "hmis_drug_issuance_data")
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
@ToString
public class DrugIssuance extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", unique = true, nullable = false)
	private String code;

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department issuedFromOutlet;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User issuedBy;

	@Column(name = "date")
	private LocalDate date = LocalDate.now();

	@OneToOne( targetEntity = DrugRequisition.class, fetch = FetchType.LAZY )
	@JoinColumn(name = "drug_requisition_id" )
	private DrugRequisition drugRequisition;

	@Column(name = "type")
	private IssuanceTypeEnum type;

}
