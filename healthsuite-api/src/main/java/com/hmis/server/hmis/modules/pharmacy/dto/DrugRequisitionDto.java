package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DrugRequisitionDto {
	private Long id;
	private DepartmentDto issuingDepartment;
	private DepartmentDto receivingDepartment;
	private DepartmentDto location;
	private UserDto operatingUser;
	private DrugRequisitionTypeEnum requisitionTypeEnum; //DrugRequisitionTypeEnum
	private Boolean isFulfilled;
	private List<DrugRequisitionItemDto> requisitionItems;
	private String code;
	private DateDto dateDto;
	private Date date;
	private DrugIssuanceDto issuance;
}
