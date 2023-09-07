package com.hmis.server.hmis.modules.shift.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ShiftCodeSearchDto {
    private Long id;
    private String shiftNumber;
    private String username;
    private String openDate;
    private String closeDate;
    private Boolean status;
    private String fullName;


    public ShiftCodeSearchDto(String shiftNumber) {
        this.shiftNumber = shiftNumber;
    }
}
