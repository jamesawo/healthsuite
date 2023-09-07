package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.model.SchemePlan;
import com.hmis.server.hmis.modules.emr.dto.PatientInsuranceDetailDto;
import com.hmis.server.hmis.modules.emr.dto.SchemeTreatmentTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table( name = "hmis_patient_insurance_detail" )
@EqualsAndHashCode( callSuper = true )
@NoArgsConstructor
@ToString
public class PatientInsuranceDetail extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	// todo remove scheme plan
	@OneToOne
	@JoinColumn
	private Scheme scheme;

	@OneToOne
	@JoinColumn
	private SchemePlan schemePlan;

	@Column( name = "primary_provider" )
	private String primaryProvider;

	@Column( name = "type_of_Care" )
	private String typeOfCare;

	@Column( name = "approval_code" )
	private String approvalCode;

	@Column( name = "diagnosis" )
	private String diagnosis;

	@Column( name = "approval_start_Date" )
	private LocalDate approvalStartDate;

	@Column( name = "approval_end_Date" )
	private LocalDate approvalEndDate;

	@Column( name = "treatment_type" )
	private SchemeTreatmentTypeEnum treatmentType;

	@Column( name = "nhis_number" )
	private String nhisNumber;

	public PatientInsuranceDetail( Long id ) {
		this.id = id;
	}

	public PatientInsuranceDetailDto transformToDto() {
		PatientInsuranceDetailDto insuranceDetailDto = new PatientInsuranceDetailDto();
		insuranceDetailDto.setId( this.getId() );
		insuranceDetailDto.setScheme( this.getScheme().mapToDto() );
		insuranceDetailDto.setPrimaryProvider( this.getPrimaryProvider() );
		insuranceDetailDto.setTypeOfCare( this.getTypeOfCare() );
		insuranceDetailDto.setApprovalCode( this.getApprovalCode() );
		insuranceDetailDto.setDiagnosis( this.getDiagnosis() );
		insuranceDetailDto.setNhisNumber( this.nhisNumber );
		insuranceDetailDto.setTreatmentType( this.treatmentType != null ? this.treatmentType.name() : null );
		LocalDate approvalStartDate = this.getApprovalStartDate();
		LocalDate approvalEndDate = this.getApprovalEndDate();
		if ( approvalStartDate != null ) {
			insuranceDetailDto.setApprovalStartDate(
					new DateDto( approvalStartDate.getYear(), approvalStartDate.getMonthValue() + 1,
					             approvalStartDate.getDayOfMonth()
					) );
		}
		if ( approvalEndDate != null ) {
			insuranceDetailDto.setApprovalEndDate(
					new DateDto( approvalEndDate.getYear(), approvalEndDate.getMonthValue() + 1,
					             approvalEndDate.getDayOfMonth()
					) );
		}
		if ( this.getSchemePlan() != null ) {
			insuranceDetailDto.setSchemePlanId( this.getSchemePlan().getId() );
		}
		return insuranceDetailDto;
	}
}
