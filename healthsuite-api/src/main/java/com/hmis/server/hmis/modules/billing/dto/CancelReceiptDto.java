package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class CancelReceiptDto {
    private Long paymentReceiptId;
    private String receiptCode;
    private UserDto cancelledBy;
    private DepartmentDto cancelledFrom;
    private String comment;
}
