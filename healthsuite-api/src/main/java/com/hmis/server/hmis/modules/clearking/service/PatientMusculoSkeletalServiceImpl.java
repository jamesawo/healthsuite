package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.MusculoSkeletalFormDto;
import com.hmis.server.hmis.modules.clearking.dto.YesNoEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkMusculoSkeletalExamination;
import com.hmis.server.hmis.modules.clearking.repository.PatientMusculoSkeletalRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientMusculoSkeletalServiceImpl {
    @Autowired
    private PatientMusculoSkeletalRepository repository;

    public ClerkMusculoSkeletalExamination save(ClerkMusculoSkeletalExamination model){
        try {
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkMusculoSkeletalExamination save(MusculoSkeletalFormDto dto){
        ClerkMusculoSkeletalExamination model = this.mapToModel(dto);
        return this.save(model);
    }

    public ClerkMusculoSkeletalExamination mapToModel(MusculoSkeletalFormDto dto){
        ClerkMusculoSkeletalExamination model = new ClerkMusculoSkeletalExamination();
        if(ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if(ObjectUtils.isNotEmpty(dto.getMuscleBulk())){
            model.setMuscleBulk(dto.getMuscleBulk());
        }
        if(ObjectUtils.isNotEmpty(dto.getSpasticity())){
            model.setSpasticity(dto.getSpasticity());
        }
        if (ObjectUtils.isNotEmpty(dto.getReflexes())){
            model.setReflexe(dto.getReflexes());
        }
        if (ObjectUtils.isNotEmpty(dto.getTone())){
            model.setTone(dto.getTone());
        }
        if (ObjectUtils.isNotEmpty(dto.getRigidity())){
            model.setRigidity(dto.getRigidity().name());
        }
        return model;
    }

    public MusculoSkeletalFormDto mapToDto(ClerkMusculoSkeletalExamination model){
        MusculoSkeletalFormDto formDto = new MusculoSkeletalFormDto();
        if(ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if(ObjectUtils.isNotEmpty(model.getMuscleBulk())){
            formDto.setMuscleBulk(model.getMuscleBulk());
        }
        if(ObjectUtils.isNotEmpty(model.getSpasticity())){
            formDto.setSpasticity(model.getSpasticity());
        }
        if (ObjectUtils.isNotEmpty(model.getReflexe())){
            formDto.setReflexes(model.getReflexe());
        }
        if (ObjectUtils.isNotEmpty(model.getTone())){
            formDto.setTone(model.getTone());
        }
        if (ObjectUtils.isNotEmpty(model.getRigidity())){
            formDto.setRigidity(YesNoEnum.valueOf(model.getRigidity()));
        }
        return formDto;
    }
}
