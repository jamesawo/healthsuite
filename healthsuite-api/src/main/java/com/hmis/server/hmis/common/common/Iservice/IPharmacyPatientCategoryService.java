package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.PharmacyPatientCategoryDto;

import java.util.List;

public interface IPharmacyPatientCategoryService {
    PharmacyPatientCategoryDto createOne(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    List<PharmacyPatientCategoryDto> createInBatch(List<PharmacyPatientCategoryDto> pharmacyPatientCategoryDtoList);

    List<PharmacyPatientCategoryDto> findAll();

    List<PharmacyPatientCategoryDto> findByPaymentMethod(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    List<PharmacyPatientCategoryDto> findByPharmacyPatientCategoryType(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    List<PharmacyPatientCategoryDto> findByNameLike(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    PharmacyPatientCategoryDto findByName(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    PharmacyPatientCategoryDto findByCode(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    PharmacyPatientCategoryDto updateOne(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    void updateInBatch(List<PharmacyPatientCategoryDto> pharmacyPatientCategoryDtoList);

    void deactivateOne(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);

    boolean isPharmacyPatientCategoryExist(PharmacyPatientCategoryDto pharmacyPatientCategoryDto);
}
