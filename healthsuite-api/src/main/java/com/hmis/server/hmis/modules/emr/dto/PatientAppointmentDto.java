package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.SpecialityUnitDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PatientAppointmentDto {

	@Positive( message = "Id must be a number" )
	private Long id;

	@NotNull( message = "Patient is required")
	private Long patientId;

	@NotNull(message = "Consultant is required")
	private Long consultantId;

	private UserDto consultant;

	@NotNull(message = "Speciality is required")
	private Long specialityId;

	private SpecialityUnitDto specialityUnit;

	@NotNull(message = "DateTime is required")
	private DateDto dateTime;

	@NotNull(message = "Clinic is required")
	private Long clinicId;

	private DepartmentDto clinic;

	@NotNull(message = "Officer on duty is required")
	private Long bookedById;

	@NotNull(message = "System Location is required")
	private Long locationId;

	private PatientAppointmentStatusEnum appointmentStatus;

	private String remarks;
}
