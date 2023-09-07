package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.MaritalStatusDto;
import com.hmis.server.hmis.common.common.model.MaritalStatus;

import java.util.List;

public interface IMaritalStatusService {
    MaritalStatusDto createOne(MaritalStatusDto maritalStatusDto);

    void createInBatch(List<MaritalStatusDto> maritalStatusDtos);

    List<MaritalStatusDto> findAll();

    MaritalStatusDto findOne(MaritalStatusDto maritalStatusDto);

    MaritalStatusDto findByName(MaritalStatusDto maritalStatusDto);

    List<MaritalStatusDto> findByNameLike(MaritalStatusDto maritalStatusDto);

    MaritalStatusDto updateOne(MaritalStatusDto maritalStatusDto);

    void updateInBatch(List<MaritalStatusDto> maritalStatusDtoList);

    void deactivateOne(MaritalStatusDto maritalStatusDto);
}
