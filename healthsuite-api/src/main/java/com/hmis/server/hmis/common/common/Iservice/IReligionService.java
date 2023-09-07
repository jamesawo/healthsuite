package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.ReligionDto;

import java.util.List;

public interface IReligionService {
    ReligionDto createOne(ReligionDto religionDto);

    List<ReligionDto> createInBatch(List<ReligionDto> religionDtoList);

    List<ReligionDto> findAll();

    List<ReligionDto> findByNameLike(ReligionDto religionDto);

    ReligionDto findByName(ReligionDto religionDto);

    ReligionDto findByCode(ReligionDto religionDto);

    ReligionDto updateOne(ReligionDto religionDto);

    List<ReligionDto> updateInBatch(List<ReligionDto> religionDtoList);

    void deactivateOne(ReligionDto religionDto);

    void deactivateInBatch(List<ReligionDto> religionDtoList);

    void activateOne(ReligionDto religionDto);

    void activateInBatch(List<ReligionDto> religionDtoList);

    boolean isReligionExist(ReligionDto religionDto);


}
