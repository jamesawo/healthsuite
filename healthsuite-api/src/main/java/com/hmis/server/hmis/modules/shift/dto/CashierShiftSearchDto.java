package com.hmis.server.hmis.modules.shift.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class CashierShiftSearchDto {
    private Long userId;
    private DateDto startDate;
    private DateDto endDate;
    private CashierShiftReportDetailTypeEnum reportType;
    private SearchShiftByEnum searchBy;
    private String shiftNumber;
    private Long shiftId;
    private FundReceptionTypeEnum receptionTypeEnum;
}
