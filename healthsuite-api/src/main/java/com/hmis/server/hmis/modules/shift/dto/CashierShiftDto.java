package com.hmis.server.hmis.modules.shift.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class CashierShiftDto {
    private Long id;
    private String shiftNumber;
    private LocalDate openDate;
    private LocalTime openTime;
    private int receiptCount;
    private LocalTime closeTime;
    private LocalDate closeDate;
    private UserDto closedByUser;
    private UserDto cashier;
    private DepartmentDto department;
    private String closeTypeEnum;
    private Boolean  isActive = true;
    private Boolean isClosedByCashier = false;
    private Boolean isFundReceived = false;
    private Boolean isShitCompiled = false;
    private CashierCompiledShiftDto compiledShift;
    private CashierFundReceptionDto fundReception;
    private double cash;
    private double cheque;
    private double pos;
    private double mobileMoney;
    private double etf;
    private double total;
}
