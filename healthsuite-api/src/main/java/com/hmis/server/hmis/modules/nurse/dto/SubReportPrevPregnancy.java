package com.hmis.server.hmis.modules.nurse.dto;

import lombok.Data;

@Data
public class SubReportPrevPregnancy {
    private String duration;
    private String outcome;
    private String gender;
    private String dateOfBirth;
    private String birthWeight;
    private String alive;
    private String comment;
}
