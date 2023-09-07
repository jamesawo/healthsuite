package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PatientBillStatsDto {
    private double totalUnPaidBills = 0;
    private double totalUnusedDeposit = 0;
    private double netBillAmount = 0;

    public PatientBillStatsDto(double totalUnusedDeposit) {
        this.totalUnusedDeposit = totalUnusedDeposit;
    }
}
