package com.hmis.server.hmis.modules.lab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.lab.dto.LabSampleSearchedByEnum;
import com.hmis.server.hmis.modules.lab.dto.NewOrEditSampleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table( name = "hmis_lab_specimen_collection_data" )
@NoArgsConstructor
@ToString
@EqualsAndHashCode( callSuper = true )
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabSpecimenCollection extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "new_or_edit_enum" )
	private NewOrEditSampleEnum newOrEditSampleEnum;

	@Column( name = "search_sample_by_enum" )
	private LabSampleSearchedByEnum searchByEnum;

	@OneToOne
	@JoinColumn( name = "lab_test_request_id" )
	private LabTestRequest labTestRequest;

	@OneToOne
	@JoinColumn( name = "lab_test_request_item_id" )
	private LabTestRequestItem labTestRequestItem;

	@Column( name = "clinical_summary", columnDefinition = "TEXT" )
	private String clinicalSummary;

	@Column( name = "provisional_diagnosis", columnDefinition = "TEXT" )
	private String provisionalDiagnosis;

	@Column( name = "other_information", columnDefinition = "TEXT" )
	private String otherInformation;

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@Column( name = "time" )
	private LocalTime time = LocalTime.now();

	@OneToOne
	@JoinColumn( name = "captured_from_dep_id" )
	private Department capturedFrom;

	@OneToOne
	@JoinColumn( name = "captured_by_user_id" )
	private User capturedBy;
}
