package com.hmis.server.hmis.common.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@NoArgsConstructor
@ToString
public class ReligionDto {
    private Optional<Long> id;
    private Optional<String> name;

}
