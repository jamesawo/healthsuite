package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.CardioVascularFormDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkCardioVascularExamination;
import com.hmis.server.hmis.modules.clearking.repository.PatientCardioVascularRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientCardioVascularServiceImpl {
    @Autowired
    private PatientCardioVascularRepository repository;

    public ClerkCardioVascularExamination save(CardioVascularFormDto dto){
        try{
            ClerkCardioVascularExamination model = this.mapToModel(dto);
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkCardioVascularExamination mapToModel(CardioVascularFormDto dto){
        ClerkCardioVascularExamination model = new ClerkCardioVascularExamination();
        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getPulseRate())){
            model.setPulseRate(dto.getPulseRate());
        }
        if (ObjectUtils.isNotEmpty(dto.getBloodPressure())){
            model.setBloodPressure(dto.getBloodPressure());
        }
        if (ObjectUtils.isNotEmpty(dto.getJugularVenousPressure())){
            model.setJugularVenousPressure(dto.getJugularVenousPressure());
        }
        if (ObjectUtils.isNotEmpty(dto.getApexBeat())){
            model.setApexBeat(dto.getApexBeat());
        }
        if (ObjectUtils.isNotEmpty(dto.getHeartSound())){
            model.setHeartSound(dto.getHeartSound());
        }
        return model;
    }


    public CardioVascularFormDto mapToDto(ClerkCardioVascularExamination model){
        CardioVascularFormDto formDto = new CardioVascularFormDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getPulseRate())){
            formDto.setPulseRate(model.getPulseRate());
        }
        if (ObjectUtils.isNotEmpty(model.getBloodPressure())){
            formDto.setBloodPressure(model.getBloodPressure());
        }
        if (ObjectUtils.isNotEmpty(model.getJugularVenousPressure())){
            formDto.setJugularVenousPressure(model.getJugularVenousPressure());
        }
        if (ObjectUtils.isNotEmpty(model.getApexBeat())){
            formDto.setApexBeat(model.getApexBeat());
        }
        if (ObjectUtils.isNotEmpty(model.getHeartSound())){
            formDto.setHeartSound(model.getHeartSound());
        }
        return formDto;
    }
}
