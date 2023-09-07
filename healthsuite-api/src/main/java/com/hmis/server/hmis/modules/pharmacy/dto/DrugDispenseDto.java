package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DrugDispenseDto {
	@NotNull( message = "Receipt Is Required")
	private Long receiptId;
	@NotNull(message = "Dispensing Outlet Is Required")
	private DepartmentDto outlet;
	private PatientDetailDto patientDetailDto;
	private PatientBillDto patientBillDto;
	private UserDto dispensedBy;
}
