package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IOrganismService;
import com.hmis.server.hmis.common.common.dto.OrganismDto;
import com.hmis.server.hmis.common.common.model.Organism;
import com.hmis.server.hmis.common.common.repository.OrganismRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrganismServiceImpl implements IOrganismService {

    private static final String title = "Organism";

    @Autowired
    OrganismRepository organismRepository;

    @Override
    public OrganismDto createOne(OrganismDto organismDto) {
        return mapModelToDto(organismRepository.save(mapDtoToModel(organismDto)));
    }

    @Override
    public List<OrganismDto> createInBatch(List<OrganismDto> organismDtoList) {
        return null;
    }

    @Override
    public List<OrganismDto> findAll() {
        return mapModelListToDtoList(organismRepository.findAll());
    }

    @Override
    public OrganismDto findOne(OrganismDto organismDto) {
        return null;
    }

    @Override
    public OrganismDto findByName(OrganismDto organismDto) {
        return null;
    }

    @Override
    public List<OrganismDto> findByNameLike(OrganismDto organismDto) {
        return null;
    }

    @Override
    public OrganismDto findByCode(OrganismDto organismDto) {
        return null;
    }

    @Override
    public OrganismDto updateOne(OrganismDto organismDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<OrganismDto> organismDtoList) {

    }

    @Override
    public void deactivateOne(OrganismDto organismDto) {

    }

    @Override
    public void deleteOne(OrganismDto organismDto) {

    }

    @Override
    public boolean isOrganismExist(OrganismDto organismDto) {
        if (organismDto.getName().isPresent()){
            return organismRepository.findAll().stream().anyMatch(labSpecimen ->
                    labSpecimen.getName().compareToIgnoreCase(organismDto.getName().get()) == 0);
        }else throw new HmisApplicationException("Provide Name Before Is Exist Check :" +title);
    }

    public OrganismDto mapModelToDto(Organism organism) {
        if (organism != null) {
            OrganismDto organismDto = new OrganismDto();
            setDto(organismDto, organism);
            return organismDto;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
    }

    private List<OrganismDto> mapModelListToDtoList(List<Organism> organismList) {
        List<OrganismDto> organismDtoList = new ArrayList<>();

        if (organismList != null && !organismList.isEmpty()) {
            organismList.forEach(organism -> {
                OrganismDto organismDto = new OrganismDto();
                setDto(organismDto, organism);
                organismDtoList.add(organismDto);
            });
            return organismDtoList;
        }
        return organismDtoList;
    }

    private void setDto(OrganismDto organismDto, Organism organism) {
        if (organism.getId() != null)
            organismDto.setId(Optional.of(organism.getId()));
        if (organism.getName() != null)
            organismDto.setName(Optional.of(organism.getName()));
    }

    public Organism mapDtoToModel(OrganismDto organismDto) {
        if (organismDto != null) {
            Organism organism = new Organism();
            setModel(organismDto, organism);
            return organism;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
    }


    private List<Organism> mapDtoListToModelList(List<OrganismDto> organismDtoList) {
        if (organismDtoList != null && !organismDtoList.isEmpty()) {
            List<Organism> organismList = new ArrayList<>();
            organismDtoList.forEach(organismDto -> {
                Organism organism = new Organism();
                setModel(organismDto, organism);
                organismList.add(organism);
            });
            return organismList;
        } else throw new HmisApplicationException("Cannot Map Empty DtoList to ModelList: " + title);
    }

    private void setModel(OrganismDto organismDto, Organism organism) {
        if (organismDto.getId().isPresent())
            organism.setId(organismDto.getId().get());

        if (organismDto.getName().isPresent())
            organism.setName(organismDto.getName().get());
    }
}
