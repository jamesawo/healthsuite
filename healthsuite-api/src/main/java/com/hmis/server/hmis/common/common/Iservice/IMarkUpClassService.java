package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.MarkUpClassDto;

import java.util.List;

public interface IMarkUpClassService {
    MarkUpClassDto createOne(MarkUpClassDto markUpClassDto);

    void createInBatch(List<MarkUpClassDto> markUpClassDtoList);

    List<MarkUpClassDto> findAll();

    MarkUpClassDto findOne(MarkUpClassDto markUpClassDto);

    MarkUpClassDto findByName(MarkUpClassDto markUpClassDto);

    List<MarkUpClassDto> findByNameLike(MarkUpClassDto markUpClassDto);

    MarkUpClassDto findByCode(MarkUpClassDto markUpClassDto);

    MarkUpClassDto updateOne(MarkUpClassDto markUpClassDto);

    void updateInBatch(List<MarkUpClassDto> markUpClassDtoList);

    void deactivateOne(MarkUpClassDto markUpClassDto);
}
