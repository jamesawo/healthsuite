package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;

@Data
public class CashierCompiledShiftReportDataDto {
    private String cashierName;
    private String shiftNumber;
    private String shiftDate;
    private double totalAmount;
}
