package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DrugIssuanceDto {
	public DepartmentDto outlet;
	private Long id;
	private IssuanceTypeEnum type;
	private DateDto startDate;
	private DateDto endDate;
	private DrugRequisitionDto requisition;
	private UserDto user;
	private String issuanceNumber;
	private DateDto dateDto;
	private Date date;


}
