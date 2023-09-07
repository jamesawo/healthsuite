package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "hmis_gender_data")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class Gender extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	public Gender(Long id) {
		this.id = id;
	}

	public Gender(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Gender(String name) {
		this.name = name;
	}
}
