package com.hmis.server.hmis.common.common.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "hmis_nursing_note_label_data" )
@EqualsAndHashCode( callSuper = true )
@NoArgsConstructor
public class NursingNoteLabel extends Auditable< String > {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( unique = true, nullable = false )
	private String name;

	@Column
	private String code;

	@Column
	private String description;
}
