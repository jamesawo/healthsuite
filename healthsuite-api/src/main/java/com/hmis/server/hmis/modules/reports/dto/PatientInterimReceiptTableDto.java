package com.hmis.server.hmis.modules.reports.dto;

import lombok.Data;

@Data
public class PatientInterimReceiptTableDto {
    private String date;
    private String description;
    private Double amount;
}
