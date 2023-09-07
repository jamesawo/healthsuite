package com.hmis.server.hmis.modules.reports.dto;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.common.dto.RevenueDepartmentDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRegisterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class DailyCashCollectionSearchDto {
    private DateDto startDate;
    private DateDto endDate;
    private RevenueDepartmentDto revenueDepartment;
    private DailyCollectionFilterTypeEnum type;
    private ProductServiceDto service;
    private DepartmentDto serviceDepartment;
    private DrugRegisterDto drug;

}
