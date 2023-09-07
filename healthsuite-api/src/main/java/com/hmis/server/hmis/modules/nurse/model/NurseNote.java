package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.NursingNoteLabel;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_nurse_note_data")
@NoArgsConstructor
@ToString
@EqualsAndHashCode( callSuper=true)
public class NurseNote extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "patient_detail_id", nullable = false)
	private PatientDetail patient;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User capturedBy;

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department captureFromLocation;

	@OneToOne
	@JoinColumn(name = "nurse_note_label_id")
	private NursingNoteLabel label;

	@Column(name = "note")
	private String note;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "time")
	private LocalTime time;
}
