package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.*;
import com.hmis.server.hmis.common.common.model.Gender;
import com.hmis.server.hmis.common.common.model.MaritalStatus;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrug;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestLab;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestRadiology;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PatientDetailDto {
	private Long patientId;
	private String patientAge;
	private DateDto patientDateOfBirth;
	@NotNull
	private String patientFirstName;
	private String patientLastName;
	private String patientOtherName;
	@NotNull
	private PatientTypeEnum patientTypeEnum;
	private String patientNumber;
	private String receiptNumber;
	@NotNull
	private PatientCategoryEnum PatientCategoryEnum;
	private Long ethnicGroupId;
	private EthnicGroupDto ethnicGroup;
	private Long departmentId;
	private Long maritalStatusId;
	private Long genderId;
	private Long religionId;
	private PatientMeansOfIdentificationDto patientMeansOfIdentification;
	private PatientContactDetailDto patientContactDetail;
	private PatientNOKDetailDto patientNokDetail;
	private PatientCardHolderInfoDto patientCardHolder;
	private PatientInsuranceDetailDto patientInsurance;
	private PatientTransferDetailDto patientTransferDetails;
	private PatientOtherDetailDto patientOtherDetails;
	private String patientFullName;
	private String patientNameAndNumber;
	private GenderDto genderDto;
	private MaritalStatusDto maritalStatusDto;
	private ReligionDto religionDto;
	private byte[] passportBase64;
	private Boolean isOnAdmission;
	private PatientAdmissionDto admission;
	private String cardNumber;
	private String folderNumber;
	private Boolean revisitStatus;
	private PatientRevisitDto patientRevisitDto;
	private int patientSchemeDiscount;
	private Double totalDepositAmount;
	private DepartmentDto department;
	private DepartmentDto registeredFrom;
	private UserDto registeredBy;
	private List<ClerkRequestLab> labRequestList;
	private List<ClerkRequestDrug> drugRequestList;
	private List<ClerkRequestRadiology> radiologyRequestList;


	public PatientDetailDto( Long id ) {
		this.patientId = id;
	}

	public void setDateOfBirthFromLocalDate( LocalDate dateOfBirth ) {
		this.patientDateOfBirth = new DateDto( dateOfBirth.getYear(), dateOfBirth.getMonthValue(),
		                                       dateOfBirth.getDayOfMonth()
		);
	}

	public void setGenderDtoFromGender( Gender gender ) {
		this.genderDto = new GenderDto( Optional.of( gender.getId() ), Optional.of( gender.getName() ) );
	}

	public void setMaritalStatusDtoFromMaritalStatus( MaritalStatus maritalStatus ) {
		this.maritalStatusDto = new MaritalStatusDto( Optional.of( maritalStatus.getId() ),
		                                              Optional.of( maritalStatus.getName() )
		);
	}
}
