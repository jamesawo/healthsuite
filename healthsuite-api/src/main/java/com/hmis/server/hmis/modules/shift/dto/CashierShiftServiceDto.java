package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;

@Data
public class CashierShiftServiceDto {
    private String title;
    private double cashTotal;
    private double chequeTotal;
    private double eftTotal;
    private double posTotal;
    private double mobileTotal;
    private double totalAmount;
}
