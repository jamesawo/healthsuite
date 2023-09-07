package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor @AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueDepartmentDto {

    private Optional<Long> id;
    private Optional<String> name;
    private Optional<String> code;
    private Optional<String> nameAndCode;
	private Boolean isAttachedToDeposit;
    private Optional<RevenueDepartmentTypeDto> revenueDepartmentTypeDto;
}
