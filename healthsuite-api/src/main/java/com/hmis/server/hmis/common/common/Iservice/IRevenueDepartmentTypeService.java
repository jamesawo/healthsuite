package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.RevenueDepartmentTypeDto;

import java.util.List;

public interface IRevenueDepartmentTypeService {
    RevenueDepartmentTypeDto createOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto);

    List<RevenueDepartmentTypeDto> createInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList);

    List<RevenueDepartmentTypeDto> findAll();

    List<RevenueDepartmentTypeDto> findByTypeLike(RevenueDepartmentTypeDto revenueDepartmentTypeDto);

    RevenueDepartmentTypeDto findOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto);

    RevenueDepartmentTypeDto updateOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto);

    List<RevenueDepartmentTypeDto> updateInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList);

    void deactivateOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto);

    void deactivateInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList);

    void activateOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto);

    void activateInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList);

    String seedDefaultRevenueDepartmentTypes();
}
