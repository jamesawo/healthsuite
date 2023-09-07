package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Entity
@Table( name = "hmis_patient_category_update_log" )
@NoArgsConstructor
@AllArgsConstructor
public class PatientCategoryUpdateLog {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@OneToOne
	@JoinColumn( name = "patient_detail_id", nullable = false )
	private PatientDetail patientDetail;

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@Column( name = "time" )
	private LocalTime time = LocalTime.now();

	@OneToOne
	@JoinColumn( name = "updated_by_user_id" )
	private User updatedBy;

	@Column( name = "prev_category_enum", nullable = false )
	private PatientCategoryEnum prevCategory;

	@OneToOne
	@JoinColumn( name = "location_department_id" )
	private Department location;

	@OneToOne
	@JoinColumn( name = "prev_insurance_detail_id" )
	private PatientInsuranceDetail prevInsuranceDetail;

}
