package com.hmis.server.hmis.common.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hmis.server.hmis.common.user.model.User;
import java.util.List;
import java.util.Optional;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_department_data" )
@EqualsAndHashCode( callSuper = true )
@ToString
public class Department extends Auditable< String > {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( nullable = false, unique = true )
	private String name;

	@Column
	private String description;

	@Column
	private String code;

	@ManyToOne
	@JsonManagedReference
	private DepartmentCategory departmentCategory;

	@JsonIgnore
	@OneToMany( mappedBy = "department", cascade = CascadeType.ALL )
	private List< User > userList;

	@Column
	@ColumnDefault("true")
	private Boolean isVisible = true;


	public Department(Long id) {
		this.id = id;
	}

	public Department(Optional< Long > id) {
		super();
	}

	public Department() {
	}

	public Department(Long id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}
}
