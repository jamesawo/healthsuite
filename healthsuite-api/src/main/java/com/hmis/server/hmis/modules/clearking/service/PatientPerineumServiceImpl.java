package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.PerieneumFormDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkPerineumExamination;
import com.hmis.server.hmis.modules.clearking.repository.PatientPerineumRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientPerineumServiceImpl {
    @Autowired
    private PatientPerineumRepository repository;

    public ClerkPerineumExamination save(ClerkPerineumExamination model){
        try {
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkPerineumExamination save(PerieneumFormDto dto){
        ClerkPerineumExamination model = this.mapToModel(dto);
        return this.save(model);
    }

    public ClerkPerineumExamination mapToModel(PerieneumFormDto dto){
        ClerkPerineumExamination model = new ClerkPerineumExamination();
        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getChaperone())){
            model.setChaperone(dto.getChaperone());
        }
        if (ObjectUtils.isNotEmpty(dto.getExternalGenitalia())){
            model.setExternalGenitalia(dto.getExternalGenitalia());
        }
        if (ObjectUtils.isNotEmpty(dto.getPerRectumExamination())){
            model.setPerRectumExamination(dto.getPerRectumExamination());
        }
        if (ObjectUtils.isNotEmpty(dto.getAnyOtherLesions())){
            model.setAnyOtherLesions(dto.getAnyOtherLesions());
        }
        if (ObjectUtils.isNotEmpty(dto.getVaginalExamination())){
            model.setVaginalExamination(dto.getVaginalExamination());
        }
        return  model;
    }

    public PerieneumFormDto mapToDto(ClerkPerineumExamination model){
        PerieneumFormDto formDto = new PerieneumFormDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getChaperone())){
            formDto.setChaperone(model.getChaperone());
        }
        if (ObjectUtils.isNotEmpty(model.getExternalGenitalia())){
            formDto.setExternalGenitalia(model.getExternalGenitalia());
        }
        if (ObjectUtils.isNotEmpty(model.getPerRectumExamination())){
            formDto.setPerRectumExamination(model.getPerRectumExamination());
        }
        if (ObjectUtils.isNotEmpty(model.getAnyOtherLesions())){
            formDto.setAnyOtherLesions(model.getAnyOtherLesions());
        }
        if (ObjectUtils.isNotEmpty(model.getVaginalExamination())){
            formDto.setVaginalExamination(model.getVaginalExamination());
        }
        return  formDto;
    }
}
