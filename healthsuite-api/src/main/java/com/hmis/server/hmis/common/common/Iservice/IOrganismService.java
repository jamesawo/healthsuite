package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.OrganismDto;

import java.util.List;

public interface IOrganismService {
    OrganismDto createOne(OrganismDto organismDto);

    List<OrganismDto> createInBatch(List<OrganismDto> organismDtoList);

    List<OrganismDto> findAll();

    OrganismDto findOne(OrganismDto organismDto);

    OrganismDto findByName(OrganismDto organismDto);

    List<OrganismDto> findByNameLike(OrganismDto organismDto);

    OrganismDto findByCode(OrganismDto organismDto);

    OrganismDto updateOne(OrganismDto organismDto);

    void updateInBatch(List<OrganismDto> organismDtoList);

    void deactivateOne(OrganismDto organismDto);

    void deleteOne(OrganismDto organismDto);

    boolean isOrganismExist(OrganismDto organismDto);
}
