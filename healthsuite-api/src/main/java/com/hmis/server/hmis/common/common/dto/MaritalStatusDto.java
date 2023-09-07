package com.hmis.server.hmis.common.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@NoArgsConstructor @AllArgsConstructor
@ToString
public class MaritalStatusDto {
    private Optional<Long> id;
    private Optional<String> name;
}
