package com.hmis.server.hmis.common.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class MarkUpClassDto {
    private Long id;
    private String name;
    private String description;

}
