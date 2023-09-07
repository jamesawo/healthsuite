package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.AntibioticsDto;
import com.hmis.server.hmis.common.common.dto.OrganismDto;
import com.hmis.server.hmis.common.common.model.Antibiotics;
import com.hmis.server.hmis.common.common.model.Organism;

import java.util.List;

public interface IAntibioticsService {
    AntibioticsDto createOne(AntibioticsDto antibioticsDto);

    void createInBatch(List<AntibioticsDto> antibioticsDtoList);

    List<AntibioticsDto> findAll();

    List<AntibioticsDto> findByOrganism(OrganismDto organismDto);

    AntibioticsDto findById(OrganismDto organismDto);

    AntibioticsDto findByName(OrganismDto organismDto);

    AntibioticsDto findByCode(OrganismDto organismDto);

    AntibioticsDto updateOne(OrganismDto organismDto);

    void updateInBatch(List<OrganismDto> organismDtoList);

    void updateAll(OrganismDto organismDto);

    void deactivateOne(OrganismDto organismDto);

    boolean isAntibioticsExist(AntibioticsDto antibioticsDto);
}
