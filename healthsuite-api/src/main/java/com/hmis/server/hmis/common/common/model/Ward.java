package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/*
	Ward object is a wrapper around department => (with department Category as Ward)
	to hold bed model and related columns such as bed count for each ward, and bed usage stats
 */
@Data
@Entity
@Table( name = "hmis_ward_data" )
@EqualsAndHashCode( callSuper = true )
@NoArgsConstructor
public class Ward extends Auditable<String>{
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "code", unique = true, nullable = false )
	private String code;

	@Column( name = "occupied_count" )
	private int occupiedCount;

	@OneToMany( mappedBy = "ward", fetch = FetchType.LAZY )
	@Column
	List<Bed> beds;

	@OneToOne()
	Department department;

	public Ward( Long id ) {
		this.id = id;
	}
}
