package com.hmis.server.hmis.modules.lab.model;

import com.hmis.server.hmis.modules.lab.dto.parasitology.LabCulture;
import com.hmis.server.hmis.modules.lab.dto.parasitology.LabMacroscopy;
import com.hmis.server.hmis.modules.lab.dto.parasitology.LabMicroscopy;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_lab_parasitology_result_template_data" )
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
public class LabParasitologyResultTemplate {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Type( type = "jsonb" )
	@Column( columnDefinition = "jsonb", name = "microscopy" )
	private LabMicroscopy microscopy;

	@Type( type = "jsonb" )
	@Column( columnDefinition = "jsonb", name = "macroscopy" )
	private LabMacroscopy macroscopy;

	@Type( type = "jsonb" )
	@Column( columnDefinition = "jsonb", name = "culture" )
	private LabCulture culture;

	@Column( name = "new_lab_note", columnDefinition = "TEXT" )
	private String newLabNote;

	@OneToOne
	@JoinColumn( name = "test_preparation_id", unique = true )
	private LabTestPreparation testPreparation;

	@Column
	private LocalDate date = LocalDate.now();

	@Column
	private LocalTime time = LocalTime.now();
}
