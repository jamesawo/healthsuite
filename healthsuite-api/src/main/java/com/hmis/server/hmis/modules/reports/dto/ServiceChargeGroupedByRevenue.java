package com.hmis.server.hmis.modules.reports.dto;

import lombok.Data;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Data
public class ServiceChargeGroupedByRevenue {
	private String revenueDepartmentName;
	private JRBeanCollectionDataSource groupByService;
}
