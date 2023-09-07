package com.hmis.server.hmis.common.common.model;

import java.util.Collection;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@Data
@Entity
@Table( name = "hmis_role_data" )
@Setter
@Getter
@ToString
public class Role  {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( unique = true, nullable = false )
	private String name;

	@Column
	private String description;

	@ManyToMany( cascade = CascadeType.MERGE, fetch = FetchType.EAGER )
	@JoinTable( name = "hmis_role_permission_data", joinColumns = @JoinColumn( name = "role_id" ), inverseJoinColumns = @JoinColumn( name = "permission_id" ) )
	private Set< Permission > permissions;

	public Role() {
	}

	public Role(long id) {
		this.id = id;
	}

	public Role(String name) {
		this.name = name;
	}

	public Role(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Role(String name, String description, Set< Permission > permissions) {
		this.name = name;
		this.description = description;
		this.permissions = permissions;
	}

	public Role(Long id, String name, String description, Set< Permission > permissions) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.permissions = permissions;
	}
}
