package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IPharmacyPatientCategoryTypeService;
import com.hmis.server.hmis.common.common.dto.PharmacyPatientCategoryTypeDto;
import com.hmis.server.hmis.common.common.model.PharmacyPatientCategoryType;
import com.hmis.server.hmis.common.common.repository.PharmacyPatientCategoryTypeRepository;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyPatientCategoryTypeServiceImpl implements IPharmacyPatientCategoryTypeService {
    private static final String title = "Pharmacy Patient Category Type";

    @Autowired
    PharmacyPatientCategoryTypeRepository pharmacyPatientCategoryTypeRepository;

    @Override
    public PharmacyPatientCategoryTypeDto createOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {
        return null;
    }

    @Override
    public List<PharmacyPatientCategoryTypeDto> createInBatch(List<PharmacyPatientCategoryTypeDto> pharmacyPatientCategoryTypeDtoList) {
        return null;
    }

    @Override
    public List<PharmacyPatientCategoryTypeDto> findAll() {
        return mapModelListToDtoList(pharmacyPatientCategoryTypeRepository.findAll());
    }

    @Override
    public PharmacyPatientCategoryTypeDto findOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {
        return null;
    }

    @Override
    public PharmacyPatientCategoryTypeDto findByName(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {
        return null;
    }

    @Override
    public PharmacyPatientCategoryTypeDto findByCode(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {
        return null;
    }

    @Override
    public PharmacyPatientCategoryTypeDto updateOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {
        return null;
    }

    @Override
    public List<PharmacyPatientCategoryTypeDto> updateInBatch(List<PharmacyPatientCategoryTypeDto> pharmacyPatientCategoryTypeDtoList) {
        return null;
    }

    @Override
    public void deactivateOne(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {

    }

    @Override
    public boolean isPharmacyPatientCategoryTypeExist(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto) {
        if (pharmacyPatientCategoryTypeDto.getName().isPresent()) {
            return pharmacyPatientCategoryTypeRepository.findAll()
                    .stream()
                    .anyMatch(pharmacyPatientCategoryType -> pharmacyPatientCategoryType.getName().compareToIgnoreCase(
                            pharmacyPatientCategoryTypeDto.getName().get()) == 0);
        }else throw new HmisApplicationException("Provide Name Before Is Exist Check: "+title);
    }

    @Override
    public String seedDefaultData() {
        if (pharmacyPatientCategoryTypeRepository.findAll().size() < 1){
            List<PharmacyPatientCategoryType> pharmacyPatientCategoryTypeList = new ArrayList<>();
            pharmacyPatientCategoryTypeList.add(new PharmacyPatientCategoryType("Out Patient"));
            pharmacyPatientCategoryTypeList.add(new PharmacyPatientCategoryType("In Patient"));
            List<PharmacyPatientCategoryType> pharmacyPatientCategoryTypeList1 = pharmacyPatientCategoryTypeRepository
                    .saveAll(pharmacyPatientCategoryTypeList);
            if (pharmacyPatientCategoryTypeList1.size() > 0)
                return HmisConstant.DEFAULT_DATA_SEED_SUCCESS;
            else return HmisConstant.DEFAULT_DATA_SEED_FAILED;

        }return HmisConstant.DEFAULT_DATA_SEED_EXIST;
    }

    public PharmacyPatientCategoryType mapDtoToModel(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto){

        if (pharmacyPatientCategoryTypeDto !=  null){

            PharmacyPatientCategoryType pharmacyPatientCategoryType = new PharmacyPatientCategoryType();
            setModel(pharmacyPatientCategoryTypeDto, pharmacyPatientCategoryType);

            return pharmacyPatientCategoryType;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR+title);
    }

    public PharmacyPatientCategoryTypeDto mapModelToDto(PharmacyPatientCategoryType pharmacyPatientCategoryType){
        if (pharmacyPatientCategoryType !=  null){
            PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto = new PharmacyPatientCategoryTypeDto();
            setDto(pharmacyPatientCategoryTypeDto, pharmacyPatientCategoryType);

            return pharmacyPatientCategoryTypeDto;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR+title);
    }

    private List<PharmacyPatientCategoryType> mapDtoListToModelList(List<PharmacyPatientCategoryTypeDto> pharmacyPatientCategoryTypeDtoList){
        if (pharmacyPatientCategoryTypeDtoList != null && !pharmacyPatientCategoryTypeDtoList.isEmpty()){
            List<PharmacyPatientCategoryType> pharmacyPatientCategoryTypeList = new ArrayList<>();
            pharmacyPatientCategoryTypeDtoList.forEach(pharmacyPatientCategoryTypeDto -> {
                PharmacyPatientCategoryType pharmacyPatientCategoryType = new PharmacyPatientCategoryType();
                setModel(pharmacyPatientCategoryTypeDto, pharmacyPatientCategoryType);
                pharmacyPatientCategoryTypeList.add(pharmacyPatientCategoryType);
            });
            return pharmacyPatientCategoryTypeList;
        }else throw new HmisApplicationException("Cannot Map Empty DtoList To ModelList: "+title);
    }


    private List<PharmacyPatientCategoryTypeDto> mapModelListToDtoList(List<PharmacyPatientCategoryType> pharmacyPatientCategoryTypeList){
        List<PharmacyPatientCategoryTypeDto> pharmacyPatientCategoryTypeDtoList = new ArrayList<>();

        if ( pharmacyPatientCategoryTypeList != null && !pharmacyPatientCategoryTypeList.isEmpty()){
                pharmacyPatientCategoryTypeList.forEach(pharmacyPatientCategoryType -> {
                PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto = new PharmacyPatientCategoryTypeDto();
                setDto(pharmacyPatientCategoryTypeDto, pharmacyPatientCategoryType);
                pharmacyPatientCategoryTypeDtoList.add(pharmacyPatientCategoryTypeDto);
            });
            return pharmacyPatientCategoryTypeDtoList;
        }
        return pharmacyPatientCategoryTypeDtoList;
//        else throw new HmisApplicationException("Cannot Map Empty ModelList To DtoList: "+title);
    }

    private void setDto(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto, PharmacyPatientCategoryType pharmacyPatientCategoryType) {
        if (pharmacyPatientCategoryType.getId() != null)
            pharmacyPatientCategoryTypeDto.setId(Optional.of(pharmacyPatientCategoryType.getId()));

        if (pharmacyPatientCategoryType.getName() != null)
            pharmacyPatientCategoryTypeDto.setName(Optional.of(pharmacyPatientCategoryType.getName()));
    }

    private void setModel(PharmacyPatientCategoryTypeDto pharmacyPatientCategoryTypeDto, PharmacyPatientCategoryType pharmacyPatientCategoryType) {
        if (pharmacyPatientCategoryTypeDto.getId().isPresent())
            pharmacyPatientCategoryType.setId(pharmacyPatientCategoryTypeDto.getId().get());

        if (pharmacyPatientCategoryTypeDto.getName().isPresent())
            pharmacyPatientCategoryType.setName(pharmacyPatientCategoryTypeDto.getName().get());
    }
}
