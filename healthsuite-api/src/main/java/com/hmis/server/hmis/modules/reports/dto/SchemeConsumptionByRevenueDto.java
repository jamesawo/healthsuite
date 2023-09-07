package com.hmis.server.hmis.modules.reports.dto;

import lombok.Data;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Data
public class SchemeConsumptionByRevenueDto {
	private String groupTitle;
	private JRBeanCollectionDataSource groupData;
}
