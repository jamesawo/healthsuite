package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.RelationshipDto;
import com.hmis.server.hmis.common.common.model.Relationship;

import java.util.List;

public interface IRelationshipService {
    RelationshipDto createOne(RelationshipDto relationshipDto);

    List<RelationshipDto> createInBatch(List<RelationshipDto> relationshipDtoList);

    List<RelationshipDto> findAll();

    Relationship findOneRaw(Long id);

    List<RelationshipDto> findByNameLike(RelationshipDto relationshipDto);

    RelationshipDto findOne(RelationshipDto relationshipDto);

    RelationshipDto findByName(RelationshipDto relationshipDto);

    RelationshipDto findByCode(RelationshipDto relationshipDto);

    RelationshipDto updateOne(RelationshipDto relationshipDto);

    List<RelationshipDto> updateInBatch(List<RelationshipDto> relationshipDtoList);

    void deactivateOne(RelationshipDto relationshipDto);

    void deactivateInBatch(List<RelationshipDto> relationshipDtoList);

    boolean isRelationshipExist(RelationshipDto relationshipDto);
}
