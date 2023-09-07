package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.PhysicalExaminationFormDto;
import com.hmis.server.hmis.modules.clearking.dto.YesNoEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkPhysicalExamination;
import com.hmis.server.hmis.modules.clearking.repository.PatientPhysicalRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientPhysicalServiceImpl{
    @Autowired
    private PatientPhysicalRepository repository;

    public ClerkPhysicalExamination save(ClerkPhysicalExamination model){
        try {
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkPhysicalExamination save(PhysicalExaminationFormDto dto){
        ClerkPhysicalExamination model = this.mapToModel(dto);
        return this.save(model);
    }

    public ClerkPhysicalExamination mapToModel(PhysicalExaminationFormDto dto){
        ClerkPhysicalExamination model = new ClerkPhysicalExamination();

        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getLevelOfConsciousness())){
            model.setLevelOfConsciousness(dto.getLevelOfConsciousness());
        }
        if (ObjectUtils.isNotEmpty(dto.getPatientType())){
            model.setPatientType(dto.getPatientType());
        }
        if (ObjectUtils.isNotEmpty(dto.getPallor())){
            model.setPallor(dto.getPallor().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getDehydration())){
            model.setDehydration(dto.getDehydration());
        }
        if (ObjectUtils.isNotEmpty(dto.getCyanosis())){
            model.setCyanosis(dto.getCyanosis().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getFebril())){
            model.setFebril(dto.getFebril().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getJaundice())){
            model.setJaundice(dto.getJaundice().name());
        }
        return model;
    }

    public PhysicalExaminationFormDto mapToDto(ClerkPhysicalExamination model){
        PhysicalExaminationFormDto formDto = new PhysicalExaminationFormDto();

        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getLevelOfConsciousness())){
            formDto.setLevelOfConsciousness(model.getLevelOfConsciousness());
        }
        if (ObjectUtils.isNotEmpty(model.getPatientType())){
            formDto.setPatientType(model.getPatientType());
        }
        if (ObjectUtils.isNotEmpty(model.getPallor())){
            formDto.setPallor(YesNoEnum.valueOf(model.getPallor()));
        }
        if (ObjectUtils.isNotEmpty(model.getDehydration())){
            formDto.setDehydration(model.getDehydration());
        }
        if (ObjectUtils.isNotEmpty(model.getCyanosis())){
            formDto.setCyanosis(YesNoEnum.valueOf(model.getCyanosis()));
        }
        if (ObjectUtils.isNotEmpty(model.getJaundice())){
            formDto.setJaundice(YesNoEnum.valueOf(model.getJaundice()));
        }
        if (ObjectUtils.isNotEmpty(model.getFebril())){
            formDto.setFebril(YesNoEnum.valueOf(model.getFebril()));
        }
        return formDto;
    }


}
