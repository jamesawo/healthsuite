package com.hmis.server.hmis.modules.shift.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true)
@JsonInclude( JsonInclude.Include.NON_NULL)
public class CashierShiftSetterDto {
    private Long id;
    private Department location;
    private User cashier;


    public CashierShiftSetterDto(Department location, User cashier) {
        this.location = location;
        this.cashier = cashier;
    }
}
