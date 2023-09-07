package com.hmis.server.hmis.modules.lab.model;


import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_lab_test_verification_data" )
@NoArgsConstructor
@ToString
public class LabTestVerification {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "time" )
	private LocalTime time = LocalTime.now();

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@OneToOne
	@JoinColumn( name = "lab_test_request_item", unique = true )
	private LabTestRequestItem labTestRequestItem;

	@OneToOne
	@JoinColumn( name = "captured_from_loc_dep_id" )
	private Department capturedFrom;

	@Column( name = "is_test_approved" )
	private Boolean isTestApproved = false;

	@OneToOne
	@JoinColumn( name = "test_approved_by_user_id" )
	private User testApprovedBy;

	@Column( name = "approval_lab_note" )
	private String approvalLabNote;

}
