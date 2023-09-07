package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationModuleDto {

    private Optional<Long> id;

    private Optional<String> moduleName;

    private Optional<String> moduleCode;

}
