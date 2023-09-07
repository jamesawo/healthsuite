package com.hmis.server.hmis.modules.reports.dto;

import lombok.Data;

@Data
public class PatientInterimBillsTableDto {
    private String date;
    private String description;
    private int quantity;
    private double discount;
    private String invoiceNumber;
    private Double amount;
    private Double runningAmount;
    private String receiptNumber;
    private String billedBy;
}
