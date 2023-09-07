package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.NeurologicalExaminationDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkNeurologicalExamination;
import com.hmis.server.hmis.modules.clearking.repository.PatientNeurologicalRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientNeurologicalServiceImpl {
    @Autowired
    private PatientNeurologicalRepository repository;

    public ClerkNeurologicalExamination save(ClerkNeurologicalExamination model){
        try {
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkNeurologicalExamination save(NeurologicalExaminationDto dto){
        ClerkNeurologicalExamination model = this.mapToModel(dto);
        return this.save(model);
    }


    public ClerkNeurologicalExamination mapToModel(NeurologicalExaminationDto dto){
        ClerkNeurologicalExamination model = new ClerkNeurologicalExamination();

        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getGait())){
            model.setGait(dto.getGait());
        }
        if (ObjectUtils.isNotEmpty(dto.getAbnormalMovement())){
            model.setAbnormalMovement(dto.getAbnormalMovement());
        }

        return model;
    }

    public NeurologicalExaminationDto mapToDto(ClerkNeurologicalExamination model){
        NeurologicalExaminationDto formDto = new NeurologicalExaminationDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getGait())){
            formDto.setGait(model.getGait());
        }
        if (ObjectUtils.isNotEmpty(model.getAbnormalMovement())){
            formDto.setAbnormalMovement(model.getAbnormalMovement());
        }
        return formDto;
    }


}
