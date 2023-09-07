package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.ISurgeryService;
import com.hmis.server.hmis.common.common.dto.SurgeryDto;
import com.hmis.server.hmis.common.common.model.Surgery;
import com.hmis.server.hmis.common.common.repository.SurgeryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurgeryServiceImpl implements ISurgeryService {

    @Autowired
    SurgeryRepository surgeryRepository;

    @Override
    public SurgeryDto createOne(SurgeryDto surgeryDto) {
        return mapModelToDto(surgeryRepository.save(mapDtoToModel(surgeryDto)));
    }

    @Override
    public List<SurgeryDto> createInBatch(List<SurgeryDto> surgeryDtoList) {
        return null;
    }

    @Override
    public List<SurgeryDto> findAll() {
        return mapModelListToDtoList(surgeryRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Surgery::getId)).collect(Collectors.toList()));
    }

    @Override
    public List<SurgeryDto> findByNameLike(SurgeryDto surgeryDto) {
        return null;
    }

    @Override
    public SurgeryDto findByName(SurgeryDto surgeryDto) {
        return null;
    }

    @Override
    public SurgeryDto findByCode(SurgeryDto surgeryDto) {
        return null;
    }

    //todo::refactor findOne block across entire system, cannot return empty object ( new SurgeryDto() )
    @Override
    public SurgeryDto findOne(SurgeryDto surgeryDto) {
        if (surgeryDto.getId().isPresent())
            return mapModelToDto(surgeryRepository.getOne(surgeryDto.getId().get()));
        else return new SurgeryDto();
    }

    @Override
    public void activateOne(SurgeryDto surgeryDto) {

    }

    @Override
    public void deactivateOne(SurgeryDto surgeryDto) {

    }

    @Override
    public void activateInBatch(List<SurgeryDto> surgeryDtoList) {

    }

    @Override
    public void deactivateInBatch(List<SurgeryDto> surgeryDtoList) {
    }

    @Override
    public boolean isSurgeryExist(SurgeryDto surgeryDto) {
        return surgeryRepository.findAll()
                .stream()
                .anyMatch(surgery -> surgery.getName()
                        .compareToIgnoreCase(surgeryDto.getName().get()) == 0);
    }

    private SurgeryDto mapModelToDto(Surgery surgery){
        SurgeryDto surgeryDto = new SurgeryDto();
        if (surgery.getId() != null)
            surgeryDto.setId(Optional.of(surgery.getId()));
        if (surgery.getName() != null)
            surgeryDto.setName(Optional.of(surgery.getName()));
        if (surgery.getCode() != null)
            surgeryDto.setCode(Optional.of(surgery.getCode()));
        return surgeryDto;
    }

    private Surgery mapDtoToModel(SurgeryDto surgeryDto){
        Surgery surgery = new Surgery();
        if (surgeryDto.getId().isPresent())
            surgery.setId(surgeryDto.getId().get());
        if (surgeryDto.getName().isPresent())
            surgery.setName(surgeryDto.getName().get());
        if (surgeryDto.getCode().isPresent())
            surgery.setCode(surgeryDto.getCode().get());
        return surgery;
    }

    private List<SurgeryDto> mapModelListToDtoList(List<Surgery> surgeryList){
        List<SurgeryDto> surgeryDtoList = new ArrayList<>();
        if (!surgeryList.isEmpty()){
            surgeryList.forEach(surgery -> {
                SurgeryDto surgeryDto = new SurgeryDto();
                if (surgery.getId() != null)
                    surgeryDto.setId(Optional.ofNullable(surgery.getId()));
                if (surgery.getName() != null)
                    surgeryDto.setName(Optional.of(surgery.getName()));
                if (surgery.getCode() != null)
                    surgeryDto.setCode(Optional.of(surgery.getCode()));
                surgeryDtoList.add(surgeryDto);
            });
        }
        return surgeryDtoList;
    }

    private List<Surgery> mapDtoListToModelList(List<SurgeryDto> surgeryDtoList){
        List<Surgery> surgeryList = new ArrayList<>();
        if (!surgeryDtoList.isEmpty()){
            surgeryDtoList.forEach(surgeryDto -> {
                Surgery surgery = new Surgery();
                if (surgeryDto.getId().isPresent())
                    surgery.setId(surgeryDto.getId().get());
                if (surgeryDto.getName().isPresent())
                    surgery.setName(surgeryDto.getName().get());
                if (surgeryDto.getCode().isPresent())
                    surgery.setCode(surgeryDto.getCode().get());
                surgeryList.add(surgery);
            });
        }
        return surgeryList;
    }

}
