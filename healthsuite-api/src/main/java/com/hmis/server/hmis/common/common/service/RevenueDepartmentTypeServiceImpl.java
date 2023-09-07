package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IRevenueDepartmentTypeService;
import com.hmis.server.hmis.common.common.dto.RevenueDepartmentDto;
import com.hmis.server.hmis.common.common.dto.RevenueDepartmentTypeDto;
import com.hmis.server.hmis.common.common.model.RevenueDepartmentType;
import com.hmis.server.hmis.common.common.repository.RevenueDepartmentTypeRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RevenueDepartmentTypeServiceImpl implements IRevenueDepartmentTypeService {

    @Autowired
    RevenueDepartmentTypeRepository revenueDepartmentTypeRepository;

    private final String title = "Revenue Department Type";

    @Override
    public RevenueDepartmentTypeDto createOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
        return null;
    }

    @Override
    public List<RevenueDepartmentTypeDto> createInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList) {
        return null;
    }

    @Override
    public List<RevenueDepartmentTypeDto> findAll() {
        return mapModelListToDtoList(revenueDepartmentTypeRepository.findAll());
    }

    @Override
    public List<RevenueDepartmentTypeDto> findByTypeLike(RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
        return null;
    }

    @Override
    public RevenueDepartmentTypeDto findOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
        return null;
    }

    @Override
    public RevenueDepartmentTypeDto updateOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
        return null;
    }

    @Override
    public List<RevenueDepartmentTypeDto> updateInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList) {
        return null;
    }

    @Override
    public void deactivateOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
    }

    @Override
    public void deactivateInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList) {
    }

    @Override
    public void activateOne(RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
    }

    @Override
    public void activateInBatch(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList) {
    }

    @Override
    public String seedDefaultRevenueDepartmentTypes() {
        if (revenueDepartmentTypeRepository.findAll().size() < 1 ){
            List<RevenueDepartmentType> revenueDepartmentTypeList = new ArrayList<>();
            revenueDepartmentTypeList.add(new RevenueDepartmentType("REVOLVING"));
            revenueDepartmentTypeList.add(new RevenueDepartmentType("NON-REVOLVING"));
            List<RevenueDepartmentType> revenueDepartmentTypes = revenueDepartmentTypeRepository.saveAll(revenueDepartmentTypeList);
            if (revenueDepartmentTypes.size() > 0) {
                return "Seeded Successfully.";
            } else return "Failed to Seed.";
        }else return title + "Is Not Empty";
    }

    public RevenueDepartmentType mapDtoToModel(RevenueDepartmentTypeDto revenueDepartmentTypeDto){
        if (revenueDepartmentTypeDto != null) {
            RevenueDepartmentType revenueDepartmentType = new RevenueDepartmentType();
            if ( revenueDepartmentTypeDto.getId() != null && revenueDepartmentTypeDto.getId().isPresent() )
                revenueDepartmentType.setId(revenueDepartmentTypeDto.getId().get());
            if (revenueDepartmentTypeDto.getName() != null && revenueDepartmentTypeDto.getName().isPresent())
                revenueDepartmentType.setName(revenueDepartmentTypeDto.getName().get());
            return revenueDepartmentType;
        }else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + this.title);
    }

    public RevenueDepartmentTypeDto mapModelToDto(RevenueDepartmentType revenueDepartmentType){
        if (revenueDepartmentType != null){
            RevenueDepartmentTypeDto revenueDepartmentTypeDto = new RevenueDepartmentTypeDto();
            setDto(revenueDepartmentTypeDto, revenueDepartmentType);
            return revenueDepartmentTypeDto;
        }else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + this.title);
    }

    private List<RevenueDepartmentType> mapDtoListToModelList(List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList){
        return null;
    }

    private List<RevenueDepartmentTypeDto> mapModelListToDtoList(List<RevenueDepartmentType> revenueDepartmentTypeList){
        List<RevenueDepartmentTypeDto> revenueDepartmentTypeDtoList = new ArrayList<>();
        if (!revenueDepartmentTypeList.isEmpty()){
            revenueDepartmentTypeList.forEach(revenueDepartmentType -> {
                RevenueDepartmentTypeDto revenueDepartmentTypeDto = new RevenueDepartmentTypeDto();
                setDto(revenueDepartmentTypeDto, revenueDepartmentType);
                revenueDepartmentTypeDtoList.add(revenueDepartmentTypeDto);
            });
        }
        return revenueDepartmentTypeDtoList;
    }

    private void setDto(RevenueDepartmentTypeDto revenueDepartmentTypeDto, RevenueDepartmentType revenueDepartmentType) {
        if (revenueDepartmentType.getId() != null)
            revenueDepartmentTypeDto.setId(java.util.Optional.of(revenueDepartmentType.getId()));
        if (revenueDepartmentType.getName() != null)
            revenueDepartmentTypeDto.setName(java.util.Optional.of(revenueDepartmentType.getName()));
    }
}
