package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.SpecialityUnitDto;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import java.util.List;

public interface ISpecialityUnitService {
    SpecialityUnitDto createOne(SpecialityUnitDto specialityUnitDto);

    List<SpecialityUnitDto> createInBatch(List<SpecialityUnitDto> specialityUnitDtoList);

    List<SpecialityUnitDto> findAll();

    List<SpecialityUnitDto> findByNameLike(SpecialityUnitDto specialityUnitDto);

    SpecialityUnitDto findByName(SpecialityUnitDto specialityUnitDto);

    SpecialityUnitDto findByCode(SpecialityUnitDto specialityUnitDto);

    SpecialityUnitDto findOne(SpecialityUnitDto specialityUnitDto);

	SpecialityUnit findOneRaw(Long id);

	SpecialityUnitDto updateOne(SpecialityUnitDto specialityUnitDto);

    List<SpecialityUnitDto> updateInBatch(List<SpecialityUnitDto> specialityUnitDtoList);

    void activateOne(SpecialityUnitDto specialityUnitDto);

    void deactivateOne(SpecialityUnitDto specialityUnitDto);

    void activateInBatch(List<SpecialityUnitDto> specialityUnitDtoList);

    void deactivatedInBatch(List<SpecialityUnitDto> specialityUnitDtoList);

    boolean isSpecialityUnitExist(SpecialityUnitDto specialityUnitDto);

	List<SpecialityUnitDto> searchByNameOrCode(String search);
}
