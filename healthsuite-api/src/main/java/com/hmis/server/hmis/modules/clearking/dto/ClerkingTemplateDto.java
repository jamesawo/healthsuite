package com.hmis.server.hmis.modules.clearking.dto;

import com.hmis.server.hmis.common.user.dto.UserDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ClerkingTemplateDto {
    private Long id;
    private String templateName;
    private LocalDate savedDate;
    private LocalTime savedTime;
    private UserDto savedBy;
    private String deskEnum;
    private String deskTitle;
    private String code;
}
