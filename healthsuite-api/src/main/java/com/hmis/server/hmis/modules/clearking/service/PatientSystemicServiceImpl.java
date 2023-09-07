package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.SystemicExaminationFormDto;
import com.hmis.server.hmis.modules.clearking.dto.YesNoEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkSystemicExamination;
import com.hmis.server.hmis.modules.clearking.repository.PatientSystemicRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientSystemicServiceImpl {
    @Autowired
    private PatientSystemicRepository repository;

    public ClerkSystemicExamination save(ClerkSystemicExamination model){
        try {
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkSystemicExamination save(SystemicExaminationFormDto dto){
        ClerkSystemicExamination model = this.mapToModel(dto);
        return this.save(model);
    }

    public ClerkSystemicExamination mapToModel(SystemicExaminationFormDto dto){
        ClerkSystemicExamination model = new ClerkSystemicExamination();
        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getDyspnoea())){
            model.setDyspnoea(dto.getDyspnoea().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getParoxysmalNocturnalDyspnoea())){
            model.setParoxysmalNocturnalDyspnoea(dto.getParoxysmalNocturnalDyspnoea().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getPositionOfTrachea())){
            model.setPositionOfTrachea(dto.getPositionOfTrachea());
        }
        if (ObjectUtils.isNotEmpty(dto.getPercussionNote())){
            model.setPercussionNote(dto.getPercussionNote().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getRespiratoryNote())){
            model.setRespiratoryRate(dto.getRespiratoryNote());
        }
        if (ObjectUtils.isNotEmpty(dto.getOrthepnoea())){
            model.setOrthopnoea(dto.getOrthepnoea().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getChestMovement())){
            model.setChestMovement(dto.getChestMovement().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getAuscultation())){
            model.setAuscultation(dto.getAuscultation().name());
        }
        return model;
    }

    public SystemicExaminationFormDto mapToDto(ClerkSystemicExamination model){
        SystemicExaminationFormDto formDto = new SystemicExaminationFormDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getDyspnoea())){
            formDto.setDyspnoea(YesNoEnum.valueOf(model.getDyspnoea()));
        }
        if (ObjectUtils.isNotEmpty(model.getParoxysmalNocturnalDyspnoea())){
            formDto.setParoxysmalNocturnalDyspnoea(YesNoEnum.valueOf(model.getParoxysmalNocturnalDyspnoea()));
        }
        if (ObjectUtils.isNotEmpty(model.getPositionOfTrachea())){
            formDto.setPositionOfTrachea(model.getPositionOfTrachea());
        }
        if (ObjectUtils.isNotEmpty(model.getPercussionNote())){
            formDto.setPercussionNote(YesNoEnum.valueOf(model.getPercussionNote()));
        }
        if (ObjectUtils.isNotEmpty(model.getRespiratoryRate())){
            formDto.setRespiratoryNote(model.getRespiratoryRate());
        }
        if (ObjectUtils.isNotEmpty(model.getOrthopnoea())){
            formDto.setOrthepnoea(YesNoEnum.valueOf(model.getOrthopnoea()));
        }
        if (ObjectUtils.isNotEmpty(model.getChestMovement())){
            formDto.setChestMovement(YesNoEnum.valueOf(model.getChestMovement()));
        }
        if (ObjectUtils.isNotEmpty(model.getAuscultation())){
            formDto.setAuscultation(YesNoEnum.valueOf(model.getAuscultation()));
        }
        return formDto;
    }
}
