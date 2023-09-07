package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;

@Data
public class CashierSummarizedReportDto {
    private String revenueDepartment;
    private double cashTotal = 0;
    private double chequeTotal = 0;
    private double eftTotal = 0;
    private double posTotal = 0;
    private double mobileTotal = 0;
    private double totalAmount = 0;
}
