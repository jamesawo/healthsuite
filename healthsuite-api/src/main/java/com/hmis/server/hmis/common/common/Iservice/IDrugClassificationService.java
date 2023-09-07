package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.DrugClassificationDto;
import com.hmis.server.hmis.common.common.model.DrugClassification;
import java.util.List;

public interface IDrugClassificationService {
    DrugClassificationDto createOne(DrugClassificationDto drugClassificationDto);

    void createInBatch(List<DrugClassificationDto> drugClassificationDtoList);

    DrugClassificationDto findOne(DrugClassificationDto drugClassificationDto);

	DrugClassification findOne(Long classificationId);

	List< DrugClassificationDto > findAll();

    DrugClassificationDto findByName(DrugClassificationDto drugClassificationDto);

	DrugClassification findByName(String name);

	DrugClassification findByNameOrCode(String name);

	DrugClassificationDto findByCode(DrugClassificationDto drugClassificationDto);

    DrugClassificationDto updateOne(DrugClassificationDto drugClassificationDto);

    DrugClassificationDto updateInBatch(List<DrugClassificationDto> drugClassificationDtoList);

    void deactivateOne(DrugClassificationDto drugClassificationDto);

    boolean isDrugClassificationExist(DrugClassificationDto drugClassificationDto);

}
