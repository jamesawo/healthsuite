package com.hmis.server.hmis.modules.emr.model;


import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import java.time.LocalDateTime;
import javax.persistence.*;

import com.hmis.server.hmis.modules.emr.dto.PatientRevisitTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_patient_visit_history_data" )
@NoArgsConstructor
public class PatientVisitHistory {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@OneToOne
	@JoinColumn( nullable = false, name = "patient_id" )
	private PatientDetail patient;

	@OneToOne
	@JoinColumn( name = "clinic", nullable = false )
	private Department clinic;

	@OneToOne( cascade = CascadeType.ALL )
	@JoinColumn( name = "revisit_by" )
	private User revisitBy;

	@Column( name = "revisit_date", nullable = false )
	private LocalDateTime revisitDate = LocalDateTime.now();

	@OneToOne
	@JoinColumn( name = "revisit_from_location" )
	private Department revisitFrom;

	@Column( name = "revisit_type" )
	@ColumnDefault(value = "0")
	private PatientRevisitTypeEnum revisitType;

	@Column( nullable = false, unique = true )
	private String code;

	public PatientVisitHistory(Long id) {
		this.id = id;
	}

}
