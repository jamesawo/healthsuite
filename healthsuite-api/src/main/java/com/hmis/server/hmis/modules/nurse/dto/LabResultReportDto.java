package com.hmis.server.hmis.modules.nurse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LabResultReportDto {
    private String parameter;
    private String result;
    private String unit;
    private String range;
}
