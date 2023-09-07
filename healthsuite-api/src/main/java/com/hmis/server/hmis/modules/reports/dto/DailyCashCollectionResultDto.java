package com.hmis.server.hmis.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class DailyCashCollectionResultDto {
    private String serialNumber;
    private String revenueDepartment;
    private String cashTotal;
    private String chequeTotal;
    private String posTotal;
    private String eftTotal;
    private String mobileMoneyTotal;
    private String cashAndPosTotal;
    private String cashAndChequeTotal;
    private String totalAmount;

    // for calculating sum
    private double cashDouble = 0;
    private double chequeDouble = 0;
    private double posDouble = 0;
    private double etfDouble = 0;
    private double mobileDouble = 0;
    private double cashAndPosDouble = 0;
    private double cashAndChequeDouble = 0;
    private double totalDouble = 0;
}
