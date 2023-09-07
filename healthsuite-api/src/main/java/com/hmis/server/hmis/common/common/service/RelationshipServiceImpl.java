package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IRelationshipService;
import com.hmis.server.hmis.common.common.dto.RelationshipDto;
import com.hmis.server.hmis.common.common.model.Relationship;
import com.hmis.server.hmis.common.common.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelationshipServiceImpl implements IRelationshipService {

    @Autowired
    RelationshipRepository relationshipRepository;

    @Override
    public RelationshipDto createOne(RelationshipDto relationshipDto) {
        Relationship relationship = relationshipRepository.save(mapDtoToModel(relationshipDto));
        return mapModelToDto(relationship);
    }

    @Override
    public List<RelationshipDto> createInBatch(List<RelationshipDto> relationshipDtoList) {
        return null;
    }

    @Override
    public List<RelationshipDto> findAll() {
        return mapModelListToDtoList(relationshipRepository.findAll()
                .stream().sorted(Comparator.comparingLong(Relationship::getId)).collect(Collectors.toList()));
    }

    @Override
    public Relationship findOneRaw(Long id){
        Optional<Relationship> optional = this.relationshipRepository.findById(id);
        if (!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RelationShip not Found");
        }
        return optional.get();
    }

    @Override
    public List<RelationshipDto> findByNameLike(RelationshipDto relationshipDto) {
        return null;
    }

    @Override
    public RelationshipDto findOne(RelationshipDto relationshipDto) {
        return null;
    }

    @Override
    public RelationshipDto findByName(RelationshipDto relationshipDto) {
        return null;
    }

    @Override
    public RelationshipDto findByCode(RelationshipDto relationshipDto) {
        return null;
    }

    @Override
    public RelationshipDto updateOne(RelationshipDto relationshipDto) {
        return null;
    }

    @Override
    public List<RelationshipDto> updateInBatch(List<RelationshipDto> relationshipDtoList) {
        return null;
    }

    @Override
    public void deactivateOne(RelationshipDto relationshipDto) {

    }

    @Override
    public void deactivateInBatch(List<RelationshipDto> relationshipDtoList) {

    }

    @Override
    public boolean isRelationshipExist(RelationshipDto relationshipDto) {
        boolean flag = false;
        if (relationshipDto.getName().isPresent()){
            for (Relationship relationship : relationshipRepository.findAll()){
                if (relationship.getName().compareToIgnoreCase(relationshipDto.getName().get()) == 0){
                    flag = true;
                }
            }
        }
        return flag;
    }

    private Relationship mapDtoToModel(RelationshipDto relationshipDto){
        Relationship relationship = new Relationship();
        if (relationshipDto.getName().isPresent())
            relationship.setName(relationshipDto.getName().get());
        if (relationshipDto.getId().isPresent())
            relationship.setId(relationshipDto.getId().get());
        return relationship;

    }

    private RelationshipDto mapModelToDto(Relationship relationship){
        RelationshipDto relationshipDto = new RelationshipDto();
        if (relationship.getName() != null)
            relationshipDto.setName(Optional.ofNullable(relationship.getName()));
        if (relationship.getId() != null)
            relationshipDto.setId(Optional.ofNullable(relationship.getId()));
        return relationshipDto;
    }

    private List<RelationshipDto> mapModelListToDtoList(List<Relationship> relationshipList){
        List<RelationshipDto> relationshipDtoList = new ArrayList<>();
        if (!relationshipList.isEmpty()){
            relationshipList.forEach(model -> {
                RelationshipDto relationshipDto = new RelationshipDto();
                relationshipDto.setName(Optional.ofNullable(model.getName()));
                relationshipDto.setId(Optional.ofNullable(model.getId()));
                relationshipDtoList.add(relationshipDto);
            });
        }
        return relationshipDtoList;
    }

    private List<Relationship> mapDtoListToModelList(List<RelationshipDto> relationshipDtoList){
        List<Relationship> relationshipList = new ArrayList<>();
        if (!relationshipDtoList.isEmpty()){
            relationshipDtoList.forEach(dto -> {
                Relationship relationship = new Relationship();
                if (dto.getId().isPresent())
                    relationship.setId(dto.getId().get());
                if (dto.getName().isPresent())
                    relationship.setName(dto.getName().get());
                relationshipList.add(relationship);
            });
        }
        return relationshipList;
    }
}
