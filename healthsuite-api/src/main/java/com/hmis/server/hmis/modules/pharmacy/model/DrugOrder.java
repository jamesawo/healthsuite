package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.others.model.Vendor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_drug_order_data")
@EqualsAndHashCode( callSuper=true)
@NoArgsConstructor
@ToString
public class DrugOrder extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", unique = true, nullable = false)
	private String code;

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor vendor;

	@Column(name = "supply_category", nullable = false)
	private String supplyCategory;

	@OneToMany(mappedBy = "drugOrder", fetch = FetchType.LAZY )
	@Column
	private List<DrugOrderItem> drugOrderItems;

	@Column(name = "date")
	private LocalDate date = LocalDate.now();

	@Column(name = "time")
	private LocalTime time = LocalTime.now();

	@Column( name = "fulfilled", nullable = false )
	@ColumnDefault( "false" )
	private Boolean fulfilled = false;


	@Column( name = "is_store", nullable = false )
	@ColumnDefault( "false" )
	private Boolean isStore = false;

}
