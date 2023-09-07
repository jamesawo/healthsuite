package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;

@Data
public class CashierShiftReconciliationReportDto {
    private String shiftNumber;
    private String reconDate;
    private String shiftDate;
    private String cashierName;
    private String reconciledBy;
    private double cashTotal;
    private double chequeTotal;
    private double posTotal;
    private double etfTotal;
    private double mobileTotal;
    private double totalAmount;
}
