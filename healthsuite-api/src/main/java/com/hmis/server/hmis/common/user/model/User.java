package com.hmis.server.hmis.common.user.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Role;
import java.time.LocalDate;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_user_data")
@NoArgsConstructor
@ToString
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column( nullable = false, unique = true, name = "user_name" )
	private String userName;

	@Column( name = "password" )
	private String password;

	@Column( name = "email" )
	@Email
	private String email;

	@Column( name = "enabled" )
	private Boolean enabled = true;

	@Column( name = "token_not_expired" )
	private Boolean tokenNotExpired = true;

	@Column( name = "account_not_expired" )
	private Boolean accountNotExpired = true;

	@Column( name = "account_non_locked" )
	private Boolean accountNonLocked = true;

	@Column( nullable = false, name = "first_name" )
	private String firstName;

	@Column( nullable = false, name = "last_name" )
	private String lastName;

	@Column( name = "phone_number" )
	private String phoneNumber;

	@Column( name = "expiry_date" )
	private LocalDate expiryDate;

	@Column( nullable = false, name = "account_type" )
	private String accountType = "user";

	@JsonManagedReference
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "hmis_user_role_data", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Collection<Role> roles;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;


	public User(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
}
