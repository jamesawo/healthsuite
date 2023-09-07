package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "hmis_drug_requisition_data" )
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
public class DrugRequisition extends Auditable< String > {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "issuing_department_id", nullable = false)
	private Department issuingDepartment;

	@OneToOne
	@JoinColumn(name = "receiving_department_id", nullable = false)
	private Department receivingDepartment;

	@OneToOne
	@JoinColumn(name = "location_department_id", nullable = false)
	private Department location;

	@OneToOne
	@JoinColumn(name = "operating_user_id", nullable = false)
	private User operatingUser;

	@Column(name = "requisition_type_enum", nullable = false)
	private String requisitionTypeEnum; //DrugRequisitionTypeEnum

	@Column(name = "is_fulfilled",  nullable = false)
	private Boolean isFulfilled = false;

	@Column(name = "auto_generated_code", nullable = false, unique = true)
	private String autoGeneratedCode;

	@OneToMany(mappedBy = "drugRequisition", fetch = FetchType.LAZY)
	@Column
	private List<DrugRequisitionItem> requisitionItems;

	@OneToOne()
	private DrugIssuance drugIssuance;

	@Column( name = "request_date", nullable = false )
	private LocalDate requestDate = LocalDate.now();


}
