package com.hmis.server.hmis.modules.lab.model;


import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.dto.LabDepartmentTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_lab_test_preparation_data" )
@NoArgsConstructor
@ToString
public class LabTestPreparation {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "time" )
	private LocalTime time = LocalTime.now();

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@OneToOne
	@JoinColumn( name = "lab_test_request_id" )
	private LabTestRequest labTestRequest; // parent test

	@OneToOne
	@JoinColumn( name = "lab_test_request_item_id" )
	private LabTestRequestItem labTestRequestItem; // specific lab test item

	@OneToOne
	@JoinColumn( name = "captured_by_user_id" )
	private User capturedBy;

	@OneToOne
	@JoinColumn( name = "captured_from_loc_dep_id" )
	private Department capturedFrom;

	@Column( name = "lab_note", columnDefinition = "TEXT" )
	private String labNote;

	@ManyToOne
	@JoinColumn( name = "patient_detail_id" )
	private PatientDetail patientDetail;

	@OneToMany( mappedBy = "testPreparation" )
	List<LabTestResult> testResults;

	// test result template type
	@Column( name = "type_enum" )
	private LabDepartmentTypeEnum typeEnum;

	// other templates
	@OneToOne( mappedBy = "testPreparation" )
	private LabParasitologyResultTemplate parasitologyResult;

}
