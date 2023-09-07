package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IReligionService;
import com.hmis.server.hmis.common.common.dto.ReligionDto;
import com.hmis.server.hmis.common.common.model.Religion;
import com.hmis.server.hmis.common.common.repository.ReligionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReligionServiceImpl implements IReligionService {

    @Autowired
    ReligionRepository religionRepository;

    @Override
    public ReligionDto createOne(ReligionDto religionDto) {
        return mapModelToDto( religionRepository.save( mapDtoToModel(religionDto) ) );
    }

    @Override
    public List<ReligionDto> createInBatch(List<ReligionDto> religionDtoList) {
        return null;
    }

    @Override
    public List<ReligionDto> findAll() {
        return mapModelListToDtoList(religionRepository.findAll());
    }

    @Override
    public List<ReligionDto> findByNameLike(ReligionDto religionDto) {
        return null;
    }

    @Override
    public ReligionDto findByName(ReligionDto religionDto) {
        return null;
    }

    @Override
    public ReligionDto findByCode(ReligionDto religionDto) {
        return null;
    }

    @Override
    public ReligionDto updateOne(ReligionDto religionDto) {
        return null;
    }

    @Override
    public List<ReligionDto> updateInBatch(List<ReligionDto> religionDtoList) {
        return null;
    }

    @Override
    public void deactivateOne(ReligionDto religionDto) {

    }

    @Override
    public void deactivateInBatch(List<ReligionDto> religionDtoList) {

    }

    @Override
    public void activateOne(ReligionDto religionDto) {

    }

    @Override
    public void activateInBatch(List<ReligionDto> religionDtoList) {

    }

    @Override
    public boolean isReligionExist(ReligionDto religionDto) {
        List<Religion> religionList = religionRepository.findAll();
        boolean flag = false;

        if (religionDto.getName().isPresent() ){
            for (Religion religion: religionList){
                if (religion.getName().compareToIgnoreCase(religionDto.getName().get() ) == 0){
                    flag = true;
                }
            }
        }
        return flag;
    }



    private Religion mapDtoToModel(ReligionDto religionDto) {
        Religion religion = new Religion();

        if (religionDto.getName().isPresent())
            religion.setName(religionDto.getName().get());
        if (religionDto.getId().isPresent())
            religion.setId(religionDto.getId().get());
        return religion;
    }

    private ReligionDto mapModelToDto(Religion religion) {
        ReligionDto religionDto = new ReligionDto();
        religionDto.setName(Optional.ofNullable(religion.getName()));
        religionDto.setId(Optional.ofNullable(religion.getId()));
        return religionDto;
    }

    private List<Religion> mapDtoListToModelList(List<ReligionDto> religionDtoList) {
        List<Religion> religionList = new ArrayList<>();
        if (!religionDtoList.isEmpty()){
            for(ReligionDto religionDto : religionDtoList){
                Religion religion = new Religion();
                religion.setId(religionDto.getId().get());
                religion.setName(religionDto.getName().get());
                religionList.add(religion);
            }
        }
        return religionList;
    }

    private List<ReligionDto> mapModelListToDtoList(List<Religion> religionList) {
        List<ReligionDto> religionDtoList = new ArrayList<>();
        if (!religionList.isEmpty()){
            for(Religion religion : religionList){
                ReligionDto religionDto = new ReligionDto();
                religionDto.setId(Optional.ofNullable(religion.getId()));
                religionDto.setName(Optional.ofNullable(religion.getName()));
                religionDtoList.add(religionDto);
            }
        }
        return religionDtoList;
    }

}
