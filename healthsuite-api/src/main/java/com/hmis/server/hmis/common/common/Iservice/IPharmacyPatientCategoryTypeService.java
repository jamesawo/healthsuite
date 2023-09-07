package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.PharmacyPatientCategoryTypeDto;

import java.util.List;

public interface IPharmacyPatientCategoryTypeService {
    PharmacyPatientCategoryTypeDto createOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    List<PharmacyPatientCategoryTypeDto> createInBatch(List<PharmacyPatientCategoryTypeDto> pharmacyPatientCategoryTypeDtoList);

    List<PharmacyPatientCategoryTypeDto> findAll();

    PharmacyPatientCategoryTypeDto findOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    PharmacyPatientCategoryTypeDto findByName(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    PharmacyPatientCategoryTypeDto findByCode(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    PharmacyPatientCategoryTypeDto updateOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    List<PharmacyPatientCategoryTypeDto> updateInBatch(List<PharmacyPatientCategoryTypeDto> pharmacyPatientCategoryTypeDtoList);

    void deactivateOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    boolean isPharmacyPatientCategoryTypeExist(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto);

    String seedDefaultData();

}
