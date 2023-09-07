package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.PharmacySupplierCategoryDto;

import java.util.List;

public interface IPharmacySupplierCategoryService {
    PharmacySupplierCategoryDto createOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    List<PharmacySupplierCategoryDto> createInBatch(List<PharmacySupplierCategoryDto> pharmacySupplierCategoryDtoList);

    List<PharmacySupplierCategoryDto> findAll();

    List<PharmacySupplierCategoryDto> findByNameLike(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    PharmacySupplierCategoryDto findOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    PharmacySupplierCategoryDto findByName(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    PharmacySupplierCategoryDto findByCode(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    PharmacySupplierCategoryDto updateOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    void updateInBatch(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    void deactivateOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto);

    void deactivateInBatch(List<PharmacySupplierCategoryDto> pharmacySupplierCategoryDtoList);

    boolean isPharmacySupplierCategoryExist(PharmacySupplierCategoryDto supplierCategoryDto);
}
