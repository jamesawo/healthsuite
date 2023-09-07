package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.common.common.service.RelationshipServiceImpl;
import com.hmis.server.hmis.modules.clearking.dto.InformantDetailsDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkInformantDetails;
import com.hmis.server.hmis.modules.clearking.repository.PatientInformantRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientInformantServiceImpl {
    @Autowired
    private PatientInformantRepository repository;
    @Autowired
    private RelationshipServiceImpl relationshipService;

    public ClerkInformantDetails save(ClerkInformantDetails model){
        try{
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkInformantDetails save(InformantDetailsDto dto){
        ClerkInformantDetails model = this.mapToModel(dto);
        return this.save(model);
    }

    public ClerkInformantDetails mapToModel(InformantDetailsDto dto){
        ClerkInformantDetails model = new ClerkInformantDetails();
        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getInformantName())){
            model.setInformantName(dto.getInformantName());
        }
        if (ObjectUtils.isNotEmpty(dto.getInformantPhoneNumber())){
            model.setInformantPhoneNumber(dto.getInformantPhoneNumber());
        }
        if (ObjectUtils.isNotEmpty(dto.getInformantRelationship())){
            model.setInformantRelationship(this.relationshipService.findOneRaw(dto.getInformantRelationship().getId()));
        }
        return model;
    }

    public InformantDetailsDto mapToDto(ClerkInformantDetails model){
        InformantDetailsDto formDto = new InformantDetailsDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getInformantName())){
            formDto.setInformantName(model.getInformantName());
        }
        if (ObjectUtils.isNotEmpty(model.getInformantPhoneNumber())){
            formDto.setInformantPhoneNumber(model.getInformantPhoneNumber());
        }
        if (ObjectUtils.isNotEmpty(model.getInformantRelationship())){
            formDto.setInformantRelationship(this.relationshipService.findOneRaw(model.getInformantRelationship().getId()));
        }
        return formDto;
    }
}
