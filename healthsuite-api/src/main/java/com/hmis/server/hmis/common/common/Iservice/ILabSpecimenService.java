package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;

import java.util.List;

public interface ILabSpecimenService {
    LabSpecimenDto createOne(LabSpecimenDto labSpecimenDto);

    void createInBatch(List<LabSpecimenDto> labSpecimenDtoList);

    List<LabSpecimenDto> findAll();

    LabSpecimenDto findOne(LabSpecimenDto labSpecimenDto);

    LabSpecimenDto findByName(LabSpecimenDto labSpecimenDto);

    LabSpecimenDto findByCode(LabSpecimenDto labSpecimenDto);

    List<LabSpecimenDto> findByNameLike(LabSpecimenDto labSpecimenDto);

    LabSpecimenDto updateOne(LabSpecimenDto labSpecimenDto);

    void updateInBatch(List<LabSpecimenDto> labSpecimenDtoList);

    void deactivateOne(LabSpecimenDto labSpecimenDto);

    boolean isLabSpecimenExist(LabSpecimenDto labSpecimenDto);
}
