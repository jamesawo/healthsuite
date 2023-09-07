package com.hmis.server.hmis.modules.shift.dto;

import lombok.Data;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.ArrayList;
import java.util.List;

@Data
public class CashierDetailedReportDto {
    private String serviceDepartmentName;
    private JRBeanCollectionDataSource serviceListCollection = new JRBeanCollectionDataSource(new ArrayList<>());
    private List<CashierShiftServiceDto> serviceList = new ArrayList<>();
}
