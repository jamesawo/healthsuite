package com.hmis.server.hmis.modules.lab.dto;

import lombok.Data;

@Data
public class LabRequestItemDto {
    private String name;
    private String specimen;
    private String comment;
    private String raisedBy;
}
