package com.hmis.server.hmis.modules.emr.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.model.*;
import com.hmis.server.hmis.modules.billing.model.PatientDepositLog;
import com.hmis.server.hmis.modules.billing.model.PatientDepositSum;
import com.hmis.server.hmis.modules.emr.dto.PatientCardHolderInfoDto;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.dto.PatientTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Table( name = "hmis_patient_detail_data" )
@EqualsAndHashCode( callSuper = true )
@NoArgsConstructor
@ToString
public class PatientDetail extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "date_of_birth" )
	private LocalDate dateOfBirth;

	@Column( name = "age" )
	private String age;

	@Column( name = "first_name" )
	private String firstName;  // dont change this property name

	@Column( name = "last_name" )
	private String lastName; // dont change this property name

	@Column( name = "other_name" )
	private String otherName;

	@Column( name = "patient_type_enum" )
	private PatientTypeEnum patientTypeEnum;

	@Column( unique = true, nullable = false, name = "patient_number" )
	private String patientNumber; // don't change this property name (used in patient search)

	@Column( name = "patient_category_enum" )
	private PatientCategoryEnum patientCategory;

	@OneToOne
	@JoinColumn( name = "pharmacy_patient_category_id" )
	private PharmacyPatientCategory pharmacyPatientCategory;

	@OneToOne
	@JoinColumn( name = "ethnic_group_id" )
	private EthnicGroup ethnicGroup;

	// patient will be registered to a department where departmentCategory is clinic
	@OneToOne
	@JoinColumn( name = "department_id" )
	private Department department;

	@OneToOne
	@JoinColumn( name = "marital_status_id" )
	private MaritalStatus maritalStatus;

	@OneToOne
	@JoinColumn( name = "gender_id" )
	private Gender gender;

	@OneToOne
	@JoinColumn( name = "religion_id" )
	private Religion religion;

	@OneToOne
	@JoinColumn( name = "patient_means_of_identification_id" )
	private PatientMeansOfIdentification patientMeansOfIdentification;

	@OneToOne
	@JoinColumn( name = "patient_contact_detail_id" )
	private PatientContactDetail patientContactDetail;

	@OneToOne
	@JoinColumn( name = "patient_nok_detail_id" )
	private PatientNOKDetail patientNokDetail;

	@OneToOne
	@JoinColumn( name = "patient_card_holder_info_id" )
	private PatientCardHolderInfo patientCardHolderInfo;

	@OneToOne
	@JoinColumn( name = "patient_insurance_detail_id" )
	private PatientInsuranceDetail patientInsuranceDetail;

	@OneToOne
	@JoinColumn( name = "patient_transfer_detail_id" )
	private PatientTransferDetail patientTransferDetail;

	@OneToOne
	@JoinColumn( name = "patient_other_detail_id" )
	private PatientOtherDetail patientOtherDetail;

	@OneToOne
	@JoinColumn( name = "patient_image" )
	private PatientImage patientImage;

	@Column( name = "card_number" )
	private String cardNumber;

	@Column( name = "folder_number" )
	private String folderNumber;

	@OneToMany( mappedBy = "patient" )
	@JsonBackReference( value = "depositLogReference" )
	private List<PatientDepositLog> patientDepositLogs;

	@OneToOne( mappedBy = "patientDetail" )
	@JsonBackReference( value = "depositSumReference" )
	private PatientDepositSum patientDepositSum;

	@Column( name = "registered_by_user_id" )
	private Long registeredBy;

	@OneToOne
	@JoinColumn( name = "staff_location_dep_id" )
	private Department registerStaffLocation;

	@Column( name = "register_date" )
	private LocalDate registerDate = LocalDate.now();

	@Column( name = "register_time" )
	private LocalTime registerTime = LocalTime.now();

	public PatientDetail( Long id ) {
		this.id = id;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName + " " + this.otherName;
	}

	public int getCurrentAge() {
		return Period.between( this.dateOfBirth, LocalDate.now() ).getYears();
	}

	public PatientDetailDto transformToDto() {
		PatientDetailDto detailDto = new PatientDetailDto();
		detailDto.setPatientId( this.id );
		detailDto.setPatientTypeEnum( this.patientTypeEnum );
		detailDto.setPatientNumber( this.patientNumber );
		detailDto.setPatientFirstName( this.getFirstName() );
		detailDto.setDateOfBirthFromLocalDate( this.dateOfBirth );
		detailDto.setPatientAge( String.valueOf( this.getCurrentAge() ) );
		detailDto.setPatientOtherName( this.otherName );
		detailDto.setPatientCategoryEnum( this.patientCategory );
		detailDto.setPatientFullName( this.getFullName() );

		if ( this.ethnicGroup != null && this.ethnicGroup.getId() != null ) {
			detailDto.setEthnicGroupId( this.ethnicGroup.getId() );
			detailDto.setEthnicGroup( this.ethnicGroup.transformToDto() );
		}
		detailDto.setDepartmentId( this.department.getId() );
		detailDto.setDepartment( new DepartmentDto( this.department.getId(), this.department.getName() ) );
		detailDto.setPatientLastName( this.lastName );
		detailDto.setReligionId( this.religion.getId() );
		detailDto.setGenderId( this.gender.getId() );
		detailDto.setPatientNameAndNumber(
				String.format( "%s, %s %s - %s", this.firstName, this.lastName, this.otherName, this.patientNumber ) );
		detailDto.setMaritalStatusId( this.maritalStatus.getId() );
		detailDto.setPatientNokDetail( this.patientNokDetail.transformToDTO() );
		detailDto.setPatientContactDetail( this.patientContactDetail.transformToDTO() );
		detailDto.setPatientMeansOfIdentification( this.patientMeansOfIdentification.transformToDTO() );
		detailDto.setGenderDtoFromGender( this.gender );
		detailDto.setMaritalStatusDtoFromMaritalStatus( this.maritalStatus );

		if ( this.patientImage != null ) {
			detailDto.setPassportBase64( this.patientImage.getData() );
		}

		if ( this.cardNumber != null ) {
			detailDto.setCardNumber( this.cardNumber );
		}

		if ( this.folderNumber != null ) {
			detailDto.setFolderNumber( this.folderNumber );
		}

		if ( isSchemePatient() ) {
			detailDto.setPatientInsurance( this.patientInsuranceDetail.transformToDto() );

			// cardHolder information
			PatientCardHolderInfo cardHolderInfo = this.patientCardHolderInfo;
			PatientCardHolderInfoDto cardHolderInfoDto = new PatientCardHolderInfoDto();
			cardHolderInfoDto.setId( Optional.of( cardHolderInfo.getId() ) );
			cardHolderInfoDto.setName( cardHolderInfo.getName() );
			cardHolderInfoDto.setInsuranceNumber( cardHolderInfo.getInsuranceNumber() );
			if ( cardHolderInfo.getCardExpiry() != null ) {
				cardHolderInfoDto.setCardExpiry( new DateDto( cardHolderInfo.getCardExpiry().getYear(),
				                                              cardHolderInfo.getCardExpiry().getMonthValue() + 1,
				                                              cardHolderInfo.getCardExpiry().getDayOfMonth()
				) );
			}
			cardHolderInfoDto.setCardHolderType(
					cardHolderInfo.getCardHolderType() != null ? cardHolderInfo.getCardHolderType() : null );
			cardHolderInfoDto.setPlaceOfWork( cardHolderInfo.getPlaceOfWork() );
			cardHolderInfoDto.setDepartment( cardHolderInfo.getDepartment() );
			detailDto.setPatientCardHolder( cardHolderInfoDto );
			if ( cardHolderInfo.isDependant() ) {
				cardHolderInfoDto.setBeneficiaryName( cardHolderInfo.getBeneficiaryName() );
				cardHolderInfoDto.setRelationShipWithCardHolder(
						cardHolderInfo.getRelationshipWithCardHolder().toDto() );
			}
		}

		return detailDto;
	}

	public boolean isSchemePatient() {
		return this.patientCategory.equals( PatientCategoryEnum.SCHEME );
	}

}
