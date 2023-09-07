package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.ActualDiagnosisFormDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkActualDiagnosis;
import com.hmis.server.hmis.modules.clearking.repository.PatientActualDiagnosisRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientActualDiagnosisServiceImpl {
    @Autowired
    private PatientActualDiagnosisRepository repository;

    public ClerkActualDiagnosis save(ActualDiagnosisFormDto formDto){
        try {
            ClerkActualDiagnosis model = this.mapToModel(formDto);
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    public ClerkActualDiagnosis findById(Long id){
        Optional<ClerkActualDiagnosis> optional = this.repository.findById(id);
        if (!optional.isPresent()){

            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    public ClerkActualDiagnosis mapToModel(ActualDiagnosisFormDto dto){
        ClerkActualDiagnosis model = new ClerkActualDiagnosis();
        if (ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getOtherDiagnosis())){
            model.setOtherDiagnosis(dto.getOtherDiagnosis());
        }
        if (ObjectUtils.isNotEmpty(dto.getDiseases())){
            model.setDiseasesList(dto.getDiseases());
        }
        return  model;
    }

    public ActualDiagnosisFormDto mapToDto(ClerkActualDiagnosis model){
        ActualDiagnosisFormDto formDto = new ActualDiagnosisFormDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getOtherDiagnosis())){
            formDto.setOtherDiagnosis(model.getOtherDiagnosis());
        }
        if (ObjectUtils.isNotEmpty(model.getDiseasesList())){
            formDto.setDiseases(model.getDiseasesList());
        }
        return  formDto;
    }

}
