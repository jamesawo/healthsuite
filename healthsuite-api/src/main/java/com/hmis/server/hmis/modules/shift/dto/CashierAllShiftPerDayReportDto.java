package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;

@Data
public class CashierAllShiftPerDayReportDto {
    private String cashierName;
    private String cashierDepartment;
    private String shiftNumber;
    private String shiftDate;
    private double cashTotal;
    private double chequeTotal;
    private double posTotal;
    private double mobileTotal;
    private double etfTotal;
    private double totalTotal;
    private int receiptCount;
    private String shiftStatus;
}
