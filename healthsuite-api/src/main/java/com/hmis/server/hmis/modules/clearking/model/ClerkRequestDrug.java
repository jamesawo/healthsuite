package com.hmis.server.hmis.modules.clearking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table( name = "hmis_clerk_request_drug_data" )
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ClerkRequestDrug {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@Column( name = "other_information" )
	private String otherInformation;

	@Column( name = "use_department_filter" )
	private Boolean useDepartmentFilter;

	@Column( name = "exclude_Zero_qty" )
	private Boolean excludeZeroQty;

	@OneToMany( mappedBy = "requestDrug" )
	@Column
	private List<ClerkRequestDrugItem> drugItems;

	@Column( name = "is_used" )
	private Boolean isUsed = false;

	@JsonIgnore
	@OneToOne
	@JoinColumn( name = "patient_detail_id" )
	private PatientDetail patientDetail;

	@JsonIgnore
	@OneToOne( mappedBy = "drugRequest" )
	private ClerkDoctorRequest doctorRequest;

	@Column( name = "request_code" )
	private String code;

	@Transient
	private UserDto physician;

	@Transient
	private Long doctorRequestId;

}
