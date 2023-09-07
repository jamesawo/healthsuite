package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IMaritalStatusService;
import com.hmis.server.hmis.common.common.dto.MaritalStatusDto;
import com.hmis.server.hmis.common.common.model.MaritalStatus;
import com.hmis.server.hmis.common.common.repository.MaritalStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MaritalStatusServiceImpl implements IMaritalStatusService {

    @Autowired
    MaritalStatusRepository maritalStatusRepository;

    @Override
    public MaritalStatusDto createOne(MaritalStatusDto maritalStatusDto) {
        return mapModelListToDtoList(maritalStatusRepository.save(mapDtoToModel(maritalStatusDto)));
    }

    @Override
    public void createInBatch(List<MaritalStatusDto> maritalStatusDtos) {

    }

    @Override
    public List<MaritalStatusDto> findAll() {
        return mapModelListToDtoList(maritalStatusRepository.findAll());
    }

    @Override
    public MaritalStatusDto findOne(MaritalStatusDto maritalStatusDto) {
        return null;
    }

    @Override
    public MaritalStatusDto findByName(MaritalStatusDto maritalStatusDto) {
        return null;
    }

    @Override
    public List<MaritalStatusDto> findByNameLike(MaritalStatusDto maritalStatusDto) {
        return null;
    }

    @Override
    public MaritalStatusDto updateOne(MaritalStatusDto maritalStatusDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<MaritalStatusDto> maritalStatusDtoList) {

    }

    @Override
    public void deactivateOne(MaritalStatusDto maritalStatusDto) {

    }

    public boolean isMaritalStatusExist(MaritalStatusDto maritalStatusDto){
        boolean flag = false;
        if (maritalStatusDto.getName().isPresent()){
            for (MaritalStatus maritalStatus :
                    maritalStatusRepository.findAll()) {
                if (maritalStatus.getName().compareToIgnoreCase(maritalStatusDto.getName().get()) == 0){
                    flag = true;
                }
            }
        }
        return flag;
    }

    private MaritalStatus mapDtoToModel(MaritalStatusDto maritalStatusDto){
        MaritalStatus maritalStatus = new MaritalStatus();
        if (maritalStatusDto.getName().isPresent())
            maritalStatus.setName(maritalStatusDto.getName().get());
        if (maritalStatusDto.getId().isPresent())
            maritalStatus.setId(maritalStatusDto.getId().get());
        return maritalStatus;
    }

    private MaritalStatusDto mapModelListToDtoList(MaritalStatus maritalStatus){
        MaritalStatusDto maritalStatusDto = new MaritalStatusDto();
        maritalStatusDto.setId(Optional.ofNullable(maritalStatus.getId()));
        maritalStatusDto.setName(Optional.ofNullable(maritalStatus.getName()));
        return maritalStatusDto;
    }

    private List<MaritalStatus> mapDtoListToModelList(List<MaritalStatusDto> maritalStatusDtoList) {
        List<MaritalStatus> maritalStatusList = new ArrayList<>();
        if (!maritalStatusDtoList.isEmpty()){
            maritalStatusDtoList.forEach(dto -> {
                MaritalStatus maritalStatus = new MaritalStatus();
                if (dto.getId().isPresent())
                    maritalStatus.setId(dto.getId().get());
                if (dto.getName().isPresent())
                    maritalStatus.setName(dto.getName().get());
                maritalStatusList.add(maritalStatus);
            });
        }
        return maritalStatusList;
    }

    private List<MaritalStatusDto> mapModelListToDtoList(List<MaritalStatus> maritalStatusList){
        List<MaritalStatusDto> maritalStatusDtoList = new ArrayList<>();
        if (!maritalStatusList.isEmpty()){
            maritalStatusList.forEach(model -> {
                MaritalStatusDto maritalStatusDto = new MaritalStatusDto();
                maritalStatusDto.setName(Optional.ofNullable(model.getName()));
                maritalStatusDto.setId(Optional.ofNullable(model.getId()));
                maritalStatusDtoList.add(maritalStatusDto);
            });
        }
        return maritalStatusDtoList;
    }

}
