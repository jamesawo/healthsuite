package com.hmis.server.hmis.modules.shift.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class CashierCompiledShiftDto {
    private Long id;
    private LocalDate compiledDate = LocalDate.now();
    private LocalTime compiledTime = LocalTime.now();
    private UserDto compiledBy;
    private String code;
    private List<CashierShiftDto> cashierShifts;
    private DepartmentDto location;
}
