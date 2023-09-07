package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_speciality_unit" )
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class SpecialityUnit extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column
    private String name;

	@Column( unique = true )
    private String code;


	public SpecialityUnit( Long id ) {
		this.id = id;
	}
}
