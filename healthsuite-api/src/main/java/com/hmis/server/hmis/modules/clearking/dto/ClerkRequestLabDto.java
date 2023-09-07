package com.hmis.server.hmis.modules.clearking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.billing.dto.BillItemDto;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.lab.dto.LabTestItemsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ClerkRequestLabDto {
    private Long id;
    private String otherInformation;
    private PatientDetailDto patient;
    private String code;
    private UserDto physician;
    private LocalDate date;
    private Long doctorRequestId;
    private DepartmentDto departmentDto;
    private String invoiceNumber;
    private Boolean isDoctorRequest;
    private List<LabTestItemsDto> testItems;
}