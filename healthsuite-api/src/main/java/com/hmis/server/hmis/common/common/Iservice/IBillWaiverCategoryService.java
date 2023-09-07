package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.BillWaiverCategoryDto;
import com.hmis.server.hmis.common.common.model.BillWaiverCategory;

import java.util.List;

public interface IBillWaiverCategoryService {
    BillWaiverCategoryDto createOne(BillWaiverCategoryDto billWaiverCategoryDto);

    void createInBatch(List<BillWaiverCategoryDto> billWaiverCategoryDtoList);

    List<BillWaiverCategoryDto> findAll();

    BillWaiverCategoryDto findOne(BillWaiverCategoryDto billWaiverCategoryDto);

    BillWaiverCategoryDto findByName(BillWaiverCategoryDto billWaiverCategoryDto);

    BillWaiverCategoryDto findByCode(BillWaiverCategoryDto billWaiverCategoryDto);

    BillWaiverCategory updateOne(BillWaiverCategory billWaiverCategory);

    void updateInBatch(List<BillWaiverCategoryDto> billWaiverCategoryDtoList);

    void deactivateOne(BillWaiverCategoryDto billWaiverCategoryDto);

    boolean isBillWaiverExist(BillWaiverCategoryDto billWaiverCategoryDto);
}
