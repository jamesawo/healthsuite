package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.SurgeryDto;

import java.util.List;

public interface ISurgeryService {
    SurgeryDto createOne(SurgeryDto surgeryDto);

    List<SurgeryDto> createInBatch(List<SurgeryDto> surgeryDtoList);

    List<SurgeryDto> findAll();

    List<SurgeryDto> findByNameLike(SurgeryDto surgeryDto);

    SurgeryDto findByName(SurgeryDto surgeryDto);

    SurgeryDto findByCode(SurgeryDto surgeryDto);

    SurgeryDto findOne(SurgeryDto surgeryDto);

    void activateOne(SurgeryDto surgeryDto);

    void deactivateOne(SurgeryDto surgeryDto);

    void activateInBatch(List<SurgeryDto> surgeryDtoList);

    void deactivateInBatch(List<SurgeryDto> surgeryDtoList);

    boolean isSurgeryExist(SurgeryDto surgeryDto);

}
