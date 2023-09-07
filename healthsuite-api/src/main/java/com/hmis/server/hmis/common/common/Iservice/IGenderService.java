package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.GenderDto;
import com.hmis.server.hmis.common.common.model.Gender;

import java.util.List;

public interface IGenderService {
    GenderDto createOne(GenderDto genderDto);

    void createInBatch(List<GenderDto> genderDtoList);

    List<GenderDto> findAll();

    GenderDto findOne(GenderDto genderDto);

    Gender findOneRaw(Long id);

    Gender findByName(GenderDto genderDto);

    GenderDto updateOne(GenderDto genderDto);

    void updateInBatch(List<GenderDto> genderDtoList);

    void deactivateOne(GenderDto genderDto);

    boolean isGenderExist(GenderDto genderDto);

}
