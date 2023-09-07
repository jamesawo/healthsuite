package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.DepartmentCategoryDto;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;

import java.util.List;
import java.util.Optional;

public interface IDepartmentCategoryService {

    DepartmentCategoryDto createOne(DepartmentCategoryDto departmentCategoryDto);

    void createInBatch(List<DepartmentCategoryDto> departmentCategoryDtoList);

    List<DepartmentCategoryDto> findAll();

    DepartmentCategoryDto findOne(DepartmentCategoryDto departmentCategoryDto);

    Optional<DepartmentCategory> findByName(DepartmentCategoryDto departmentCategoryDto);

    DepartmentCategory findByName(String categoryName);

    DepartmentCategoryDto findByCode(DepartmentCategoryDto departmentCategoryDto);

    DepartmentCategoryDto updateOne(DepartmentCategoryDto departmentCategoryDto);

    void updateInBatch(DepartmentCategoryDto departmentCategoryDto);

    void deactivateOne(DepartmentCategoryDto departmentCategoryDto);

    DepartmentCategory findOneDepartmentCategory(DepartmentCategoryDto departmentCategoryDto);

}
