package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IAntibioticsService;
import com.hmis.server.hmis.common.common.dto.AntibioticsDto;
import com.hmis.server.hmis.common.common.dto.OrganismDto;
import com.hmis.server.hmis.common.common.model.Antibiotics;
import com.hmis.server.hmis.common.common.repository.AntibioticsRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AntibioticsServiceImpl implements IAntibioticsService {
    private static final String title = "Antibiotics";

    @Autowired
    AntibioticsRepository antibioticsRepository;

    @Autowired
    OrganismServiceImpl organismService;

    @Override
    public AntibioticsDto createOne(AntibioticsDto antibioticsDto) {
        return mapModelToDto(antibioticsRepository.save(mapDtoToModel(antibioticsDto)));
    }

    @Override
    public void createInBatch(List<AntibioticsDto> antibioticsDtoList) {

    }

    @Override
    public List<AntibioticsDto> findAll() {
        return mapModelListToDtoList(antibioticsRepository.findAll());
    }

    @Override
    public List<AntibioticsDto> findByOrganism(OrganismDto organismDto) {
        return null;
    }

    @Override
    public AntibioticsDto findById(OrganismDto organismDto) {
        return null;
    }

    @Override
    public AntibioticsDto findByName(OrganismDto organismDto) {
        return null;
    }

    @Override
    public AntibioticsDto findByCode(OrganismDto organismDto) {
        return null;
    }

    @Override
    public AntibioticsDto updateOne(OrganismDto organismDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<OrganismDto> organismDtoList) {

    }

    @Override
    public void updateAll(OrganismDto organismDto) {

    }

    @Override
    public void deactivateOne(OrganismDto organismDto) {

    }

    @Override
    public boolean isAntibioticsExist(AntibioticsDto antibioticsDto) {
        if (antibioticsDto.getName().isPresent()){
            boolean flag = false;
            for (Antibiotics antibiotics: antibioticsRepository.findAll()){
                if (antibiotics.getName().compareToIgnoreCase(antibioticsDto.getName().get()) == 0){
                    if (antibioticsDto.getOrganism().isPresent()){
                        Long id = antibiotics.getOrganism().getId();
                        Long id1 = antibioticsDto.getOrganism().get().getId().get();
                        if (id.equals(id1)){
                            flag = true;
                        }

                    }
                }
            }
            return flag;
        }
        throw new HmisApplicationException(HmisExceptionMessage.IS_EXIST_CHECK_ERROR + title);
    }

    private AntibioticsDto mapModelToDto(Antibiotics antibiotics) {
        if (antibiotics != null) {
            AntibioticsDto antibioticsDto = new AntibioticsDto();
            setDto(antibioticsDto, antibiotics);
            return antibioticsDto;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
    }

    private List<AntibioticsDto> mapModelListToDtoList(List<Antibiotics> antibioticsList) {
        List<AntibioticsDto> antibioticsDtoList = new ArrayList<>();

        if (antibioticsList != null && !antibioticsList.isEmpty()) {
            antibioticsList.forEach(antibiotics -> {
                AntibioticsDto antibioticsDto = new AntibioticsDto();
                setDto(antibioticsDto, antibiotics);
                antibioticsDtoList.add(antibioticsDto);
            });
            return antibioticsDtoList;
        }
        return antibioticsDtoList;
    }

    private void setDto(AntibioticsDto antibioticsDto, Antibiotics antibiotics) {
        if (antibiotics.getId() != null)
            antibioticsDto.setId(Optional.of(antibiotics.getId()));
        if (antibiotics.getName() != null)
            antibioticsDto.setName(Optional.of(antibiotics.getName()));
        if (antibiotics.getOrganism() != null)
            antibioticsDto.setOrganism(Optional.ofNullable(organismService.mapModelToDto(antibiotics.getOrganism())));
    }

    private Antibiotics mapDtoToModel(AntibioticsDto antibioticsDto) {
        if (antibioticsDto != null) {
            Antibiotics antibiotics = new Antibiotics();

            setModel(antibioticsDto, antibiotics);

            return antibiotics;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
    }


    private List<Antibiotics> mapDtoListToModelList(List<AntibioticsDto> antibioticsDtoList) {
        if (antibioticsDtoList != null && !antibioticsDtoList.isEmpty()) {
            List<Antibiotics> antibioticsList = new ArrayList<>();
            antibioticsDtoList.forEach(antibioticsDto -> {
                Antibiotics antibiotics = new Antibiotics();
                setModel(antibioticsDto, antibiotics);
                antibioticsList.add(antibiotics);
            });
            return antibioticsList;
        } else throw new HmisApplicationException("Cannot Map Empty DtoList to ModelList: " + title);
    }

    private void setModel(AntibioticsDto antibioticsDto, Antibiotics antibiotics) {
        if (antibioticsDto.getId().isPresent())
            antibiotics.setId(antibioticsDto.getId().get());

        if (antibioticsDto.getName().isPresent())
            antibiotics.setName(antibioticsDto.getName().get());

        if (antibioticsDto.getOrganism().isPresent())
            antibiotics.setOrganism(organismService.mapDtoToModel(antibioticsDto.getOrganism().get()));
    }





}
