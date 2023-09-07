package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IBillWaiverCategoryService;
import com.hmis.server.hmis.common.common.dto.BillWaiverCategoryDto;
import com.hmis.server.hmis.common.common.model.BillWaiverCategory;
import com.hmis.server.hmis.common.common.repository.BillWaiverCategoryRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BillWaiverCategoryServiceImpl implements IBillWaiverCategoryService {

    private static final String title = "BillWaiverCategory";

    @Autowired
    BillWaiverCategoryRepository billWaiverCategoryRepository;

    @Override
    public BillWaiverCategoryDto createOne(BillWaiverCategoryDto billWaiverCategoryDto) {
        return mapModelToDto(billWaiverCategoryRepository.save(mapDtoToModel(billWaiverCategoryDto)));
    }

    @Override
    public void createInBatch(List<BillWaiverCategoryDto> billWaiverCategoryDtoList) {

    }

    @Override
    public List<BillWaiverCategoryDto> findAll() {
        return mapModelListToDtoList(billWaiverCategoryRepository.findAll());
    }

    @Override
    public BillWaiverCategoryDto findOne(BillWaiverCategoryDto billWaiverCategoryDto) {
        return null;
    }

    @Override
    public BillWaiverCategoryDto findByName(BillWaiverCategoryDto billWaiverCategoryDto) {
        return null;
    }

    @Override
    public BillWaiverCategoryDto findByCode(BillWaiverCategoryDto billWaiverCategoryDto) {
        return null;
    }

    @Override
    public BillWaiverCategory updateOne(BillWaiverCategory billWaiverCategory) {
        return null;
    }

    @Override
    public void updateInBatch(List<BillWaiverCategoryDto> billWaiverCategoryDtoList) {

    }

    @Override
    public void deactivateOne(BillWaiverCategoryDto billWaiverCategoryDto) {

    }

    @Override
    public boolean isBillWaiverExist(BillWaiverCategoryDto billWaiverCategoryDto) {
        if (billWaiverCategoryDto.getName().isPresent()){
            return billWaiverCategoryRepository.findAll().stream().anyMatch(labSpecimen ->
                    labSpecimen.getName().compareToIgnoreCase(billWaiverCategoryDto.getName().get()) == 0);
        }else throw new HmisApplicationException("Provide Name Before Is Exist Check :" +title);
    }

    public BillWaiverCategoryDto mapModelToDto(BillWaiverCategory billWaiverCategory) {
        if (billWaiverCategory != null) {
            BillWaiverCategoryDto billWaiverCategoryDto = new BillWaiverCategoryDto();
            setDto(billWaiverCategoryDto, billWaiverCategory);

            return billWaiverCategoryDto;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
    }

    private List<BillWaiverCategoryDto> mapModelListToDtoList(List<BillWaiverCategory> billWaiverCategoryList) {
        List<BillWaiverCategoryDto> billWaiverCategoryDtoList = new ArrayList<>();
        if (billWaiverCategoryList != null && !billWaiverCategoryList.isEmpty()) {
            billWaiverCategoryList.forEach(billWaiverCategory -> {
                BillWaiverCategoryDto billWaiverCategoryDto = new BillWaiverCategoryDto();
                setDto(billWaiverCategoryDto, billWaiverCategory);
                billWaiverCategoryDtoList.add(billWaiverCategoryDto);
            });
            return billWaiverCategoryDtoList;
        }
        return billWaiverCategoryDtoList;
    }

    private void setDto(BillWaiverCategoryDto billWaiverCategoryDto, BillWaiverCategory billWaiverCategory) {
        if (billWaiverCategory.getId() != null)
            billWaiverCategoryDto.setId(Optional.of(billWaiverCategory.getId()));
        if (billWaiverCategory.getName() != null)
            billWaiverCategoryDto.setName(Optional.of(billWaiverCategory.getName()));
    }

    public BillWaiverCategory mapDtoToModel(BillWaiverCategoryDto billWaiverCategoryDto) {
        if (billWaiverCategoryDto != null) {
            BillWaiverCategory billWaiverCategory = new BillWaiverCategory();

            setModel(billWaiverCategoryDto, billWaiverCategory);

            return billWaiverCategory;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
    }


    private List<BillWaiverCategory> mapDtoListToModelList(List<BillWaiverCategoryDto> billWaiverCategoryDtoList) {
        if (billWaiverCategoryDtoList != null && !billWaiverCategoryDtoList.isEmpty()) {
            List<BillWaiverCategory> billWaiverCategoryList = new ArrayList<>();
            billWaiverCategoryDtoList.forEach(billWaiverCategoryDto -> {
                BillWaiverCategory billWaiverCategory = new BillWaiverCategory();
                setModel(billWaiverCategoryDto, billWaiverCategory);
                billWaiverCategoryList.add(billWaiverCategory);
            });
            return billWaiverCategoryList;
        } else throw new HmisApplicationException("Cannot Map Empty DtoList to ModelList: " + title);
    }

    private void setModel(BillWaiverCategoryDto billWaiverCategoryDto, BillWaiverCategory billWaiverCategory) {
        if (billWaiverCategoryDto.getId().isPresent())
            billWaiverCategory.setId(billWaiverCategoryDto.getId().get());

        if (billWaiverCategoryDto.getName().isPresent())
            billWaiverCategory.setName(billWaiverCategoryDto.getName().get());
    }
}
