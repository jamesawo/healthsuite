package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.dto.WaitingStatusEnum;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table( name = "hmis_nurse_waiting_list_history_data")
@NoArgsConstructor
@ToString
public class NurseWaitingListHistory {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "waiting_status_enum")
	private WaitingStatusEnum waitingStatusEnum;

	@OneToOne
	@JoinColumn(name = "patient_detail", nullable = false)
	private PatientDetail patientDetail;

	@OneToOne
	@JoinColumn(name = "clinic_department_id")
	private Department clinic;

	@Column(name = "enter_date")
	private LocalDate enterDate;

	@Column(name = "enter_time")
	private LocalTime enterTime;

	@Column(name = "attended_date")
	private LocalDate attendedDate;

	@Column(name = "attended_time")
	private LocalTime attendedTime;

	@OneToOne
	@JoinColumn(name = "attended_user_id")
	private User attendedByUser;

}
