package com.hmis.server.hmis.modules.lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkRequestLabDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabBillTestRequestDto {
    private Long id;
    private String invoiceNumber;
    private String receiptNumber;
    private String labNumber;
    private LocalDate date;
    private LocalTime time;
    private PatientDetailDto patient;
    private ClerkRequestLabDto requestLab;
    private Boolean isDoctorRequest;
    private PatientBillDto bill;
    private String code;
    private List<LabBillTestItemsDto> testItems;

    public LabBillTestRequestDto(Long id, LocalDate date, List<LabBillTestItemsDto> testItems) {
        this.id = id;
        this.date = date;
        this.testItems = testItems;
    }
}
