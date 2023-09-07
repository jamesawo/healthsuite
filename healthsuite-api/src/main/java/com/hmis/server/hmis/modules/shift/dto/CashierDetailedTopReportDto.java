package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;

@Data
public class CashierDetailedTopReportDto {
    private String serialNumber;
    private String transactionDate;
    private String receiptNumber;
    private String payer;
    private String patientNumber;
    private String location;
    private double cashTotal;
    private double chequeTotal;
    private double eftTotal;
    private double posTotal;
    private double mobileTotal;
    private double totalAmount;
    private Boolean isCancelled;
}
