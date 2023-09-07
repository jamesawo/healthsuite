package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.DiseaseDto;
import com.hmis.server.hmis.common.common.model.Disease;

import java.util.List;

public interface IDiseaseService {
    DiseaseDto createOne(DiseaseDto dto);

    List<DiseaseDto> findAll();

    DiseaseDto findOne(Long id);

    Disease findOneRaw(Long id);
}
