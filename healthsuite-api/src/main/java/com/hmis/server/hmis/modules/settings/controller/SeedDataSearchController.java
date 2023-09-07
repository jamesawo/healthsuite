package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.RevenueDepartmentDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.RevenueDepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "seed-data-search/")
public class SeedDataSearchController {

    @Autowired
    private RevenueDepartmentServiceImpl revenueDepartmentService;

    @Autowired
    private DepartmentServiceImpl serviceDepartment;

    //search revenue department
    @GetMapping("search-revenue-department")
    public List<RevenueDepartmentDto> searchRevenueDepartment(@RequestParam(value = "search") String search) {
        return this.revenueDepartmentService.searchRevenueDepartment(search);
    }

    //search service department
    @GetMapping("search-service-department")
    public List<DepartmentDto> searchServiceDepartment(@RequestParam(value = "search") String search,
                                                       @RequestParam(value = "searchByCategory", required = false) boolean searchByCategory,
                                                       @RequestParam(value = "categoryEnum", required = false) DepartmentCategoryEnum categoryEnum) {

        if (ObjectUtils.isNotEmpty(searchByCategory) && searchByCategory && ObjectUtils.isNotEmpty(categoryEnum)) {
            return this.serviceDepartment.searchServiceDepartmentByCategory(search, categoryEnum);
        } else {
            return this.serviceDepartment.searchServiceDepartment(search);
        }
    }
}
