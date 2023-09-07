package com.hmis.server.hmis.modules.pharmacy.dto;

import lombok.Data;

@Data
public class DrugRequestDto {
    private String name;
    private String dosage;
    private String days;
    private String frequency;
    private String adminRoute;
    private String raisedBy;
}
