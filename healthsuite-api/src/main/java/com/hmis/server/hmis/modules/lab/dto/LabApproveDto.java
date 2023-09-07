package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabApproveDto {
    private Long testItemId;
    private UserDto approvedBy;
    private DepartmentDto approvedFrom;
    private String labNote;
    private String pathologistComment;
    private LabVerificationViewEnum viewType;
}
