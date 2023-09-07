package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.DrugFormulationDto;
import com.hmis.server.hmis.common.common.model.DrugFormulation;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;
import java.util.List;

public interface IDrugFormulationService {

    DrugFormulationDto createOne(DrugFormulationDto drugFormulationDto);

    void createInBatch(List<DrugFormulationDto> drugFormulationDtoList);

    DrugFormulationDto findOne(DrugFormulationDto drugFormulationDto) throws EntityNotFoundException;

	DrugFormulation findOne(Long formulationId);

	DrugFormulationDto findByName(DrugFormulationDto drugFormulationDto);

	DrugFormulation findByName(String name);

	List< DrugFormulationDto > findAll();

    DrugFormulationDto updateOne(DrugFormulationDto drugFormulationDto);

    void updateInBatch(List<DrugFormulationDto> drugFormulationDtoList);

    void deactivateOne(DrugFormulationDto drugFormulationDto);

    boolean isDrugFormulationExist(DrugFormulationDto drugFormulationDto) throws BadRequestException;
}
