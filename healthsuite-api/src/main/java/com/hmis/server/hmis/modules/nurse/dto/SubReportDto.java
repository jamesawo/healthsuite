package com.hmis.server.hmis.modules.nurse.dto;

import lombok.Data;
import org.springframework.context.annotation.Description;

@Data
public class SubReportDto {
    private String subRepDate;
    //"Wrap subReportData in new JRBeanCollectionDataSource"
    private Object subReportData;
    private Object subReportFile;

    public SubReportDto(Object subReportData, Object subReportFile, String date) {
        this.subReportData = subReportData;
        this.subReportFile = subReportFile;
        this.subRepDate = date;
    }

    public SubReportDto() {
    }
}
