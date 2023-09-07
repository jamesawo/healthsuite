package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IPharmacySupplierCategoryService;
import com.hmis.server.hmis.common.common.dto.PharmacySupplierCategoryDto;
import com.hmis.server.hmis.common.common.model.PharmacySupplierCategory;
import com.hmis.server.hmis.common.common.repository.PharmacySupplierCategoryRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacySupplierCategoryServiceImpl implements IPharmacySupplierCategoryService {

    private static final String title = "Pharmacy Supplier Category";

    @Autowired
    PharmacySupplierCategoryRepository pharmacySupplierCategoryRepository;

    @Override
    public PharmacySupplierCategoryDto createOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        return mapModelToDto(pharmacySupplierCategoryRepository.save(mapDtoToModel(pharmacySupplierCategoryDto)));
    }

    @Override
    public List<PharmacySupplierCategoryDto> createInBatch(List<PharmacySupplierCategoryDto> pharmacySupplierCategoryDtoList) {
        return null;
    }

    @Override
    public List<PharmacySupplierCategoryDto> findAll() {
        return mapModelListToDtoList(pharmacySupplierCategoryRepository.findAll());
    }

    @Override
    public List<PharmacySupplierCategoryDto> findByNameLike(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        return null;
    }

    @Override
    public PharmacySupplierCategoryDto findOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        return null;
    }

    @Override
    public PharmacySupplierCategoryDto findByName(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        return null;
    }

    @Override
    public PharmacySupplierCategoryDto findByCode(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        return null;
    }

    @Override
    public PharmacySupplierCategoryDto updateOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        return null;
    }

    @Override
    public void updateInBatch(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {

    }

    @Override
    public void deactivateOne(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {

    }

    @Override
    public void deactivateInBatch(List<PharmacySupplierCategoryDto> pharmacySupplierCategoryDtoList) {

    }

    @Override
    public boolean isPharmacySupplierCategoryExist(PharmacySupplierCategoryDto supplierCategoryDto) {
        if (supplierCategoryDto.getName().isPresent()) {
            return pharmacySupplierCategoryRepository.findAll().stream().anyMatch(pharmacySupplierCategory ->
                    pharmacySupplierCategory.getName().compareToIgnoreCase(supplierCategoryDto.getName().get()) == 0);
        }else throw new HmisApplicationException("Provide Pharmacy Supplier Category Name Before Is Exist Check");
    }

    private PharmacySupplierCategoryDto mapModelToDto(PharmacySupplierCategory pharmacySupplierCategory) {
        if (pharmacySupplierCategory != null) {
            PharmacySupplierCategoryDto pharmacySupplierCategoryDto = new PharmacySupplierCategoryDto();
            setDto(pharmacySupplierCategoryDto, pharmacySupplierCategory);

            return pharmacySupplierCategoryDto;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
    }

    private List<PharmacySupplierCategoryDto> mapModelListToDtoList(List<PharmacySupplierCategory> pharmacySupplierCategoryList) {
        List<PharmacySupplierCategoryDto> pharmacySupplierCategoryDtoList = new ArrayList<>();

        if (pharmacySupplierCategoryList != null && !pharmacySupplierCategoryList.isEmpty()) {
            pharmacySupplierCategoryList.forEach(pharmacySupplierCategory -> {
                PharmacySupplierCategoryDto pharmacySupplierCategoryDto = new PharmacySupplierCategoryDto();
                setDto(pharmacySupplierCategoryDto, pharmacySupplierCategory);
                pharmacySupplierCategoryDtoList.add(pharmacySupplierCategoryDto);
            });
            return pharmacySupplierCategoryDtoList;
        }
        return pharmacySupplierCategoryDtoList;
//        else throw new HmisApplicationException("Cannot Map Empty ModelList To DtoList : " + title);
    }

    private void setDto(PharmacySupplierCategoryDto pharmacySupplierCategoryDto, PharmacySupplierCategory pharmacySupplierCategory) {
        if (pharmacySupplierCategory.getId() != null)
            pharmacySupplierCategoryDto.setId(Optional.of(pharmacySupplierCategory.getId()));
        if (pharmacySupplierCategory.getName() != null)
            pharmacySupplierCategoryDto.setName(Optional.of(pharmacySupplierCategory.getName()));
    }

    private PharmacySupplierCategory mapDtoToModel(PharmacySupplierCategoryDto pharmacySupplierCategoryDto) {
        if (pharmacySupplierCategoryDto != null) {
            PharmacySupplierCategory pharmacySupplierCategory = new PharmacySupplierCategory();

            setModel(pharmacySupplierCategoryDto, pharmacySupplierCategory);

            return pharmacySupplierCategory;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
    }


    private List<PharmacySupplierCategory> mapDtoListToModelList(List<PharmacySupplierCategoryDto> pharmacySupplierCategoryDtoList) {
        if (pharmacySupplierCategoryDtoList != null && !pharmacySupplierCategoryDtoList.isEmpty()) {
            List<PharmacySupplierCategory> pharmacySupplierCategoryList = new ArrayList<>();
            pharmacySupplierCategoryDtoList.forEach(pharmacySupplierCategoryDto -> {
                PharmacySupplierCategory pharmacySupplierCategory = new PharmacySupplierCategory();
                setModel(pharmacySupplierCategoryDto, pharmacySupplierCategory);
                pharmacySupplierCategoryList.add(pharmacySupplierCategory);
            });
            return pharmacySupplierCategoryList;
        } else throw new HmisApplicationException("Cannot Map Empty DtoList to ModelList: " + title);
    }

    private void setModel(PharmacySupplierCategoryDto pharmacySupplierCategoryDto, PharmacySupplierCategory pharmacySupplierCategory) {
        if (pharmacySupplierCategoryDto.getId().isPresent())
            pharmacySupplierCategory.setId(pharmacySupplierCategoryDto.getId().get());

        if (pharmacySupplierCategoryDto.getName().isPresent())
            pharmacySupplierCategory.setName(pharmacySupplierCategoryDto.getName().get());
    }


}
