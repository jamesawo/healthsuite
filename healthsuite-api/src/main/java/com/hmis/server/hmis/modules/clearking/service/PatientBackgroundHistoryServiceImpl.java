package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.BackgroundHistoryFormDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkBackgroundHistory;
import com.hmis.server.hmis.modules.clearking.repository.PatientBackgroundHistoryRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class PatientBackgroundHistoryServiceImpl {
    @Autowired
    private PatientBackgroundHistoryRepository repository;

    public ClerkBackgroundHistory findById(Long id){
        Optional<ClerkBackgroundHistory> optional = this.repository.findById(id);
        if(!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    public ClerkBackgroundHistory save(BackgroundHistoryFormDto dto){
        ClerkBackgroundHistory model = this.mapToModel(dto);
        return this.repository.save(model);
    }


    public ClerkBackgroundHistory mapToModel(BackgroundHistoryFormDto dto){
        ClerkBackgroundHistory model = new ClerkBackgroundHistory();
        if(ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if(ObjectUtils.isNotEmpty(dto.getPresentingComplaint())){
            model.setPresentingComplaint(dto.getPresentingComplaint());
        }
        if(ObjectUtils.isNotEmpty(dto.getHistoryOfPresentingComplaint())){
            model.setHistoryOfPresentingComplaint(dto.getHistoryOfPresentingComplaint());
        }
        if(ObjectUtils.isNotEmpty(dto.getReviewOfSystem())){
            model.setReviewOfSystem(dto.getReviewOfSystem());
        }
        if(ObjectUtils.isNotEmpty(dto.getPastMedicalAndSurgicalHistory())){
            model.setPastMedicalAndSurgicalHistory(dto.getPastMedicalAndSurgicalHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getPsychiatricHistory())){
            model.setPsychiatricHistory(dto.getPsychiatricHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getGynaecologyHistory())){
            model.setObstetricsAndGynaecologyHistory(dto.getGynaecologyHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getPaediatricHistory())){
            model.setPaediatricHistory(dto.getPaediatricHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getDrugHistory())){
            model.setDrugHistory(dto.getDrugHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getImmunizationHistory())){
            model.setImmunizationHistory(dto.getImmunizationHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getTravelHistory())){
            model.setTravelHistory(dto.getTravelHistory());
        }
        if(ObjectUtils.isNotEmpty(dto.getFamilyHistory())){
            model.setFamilyHistory(dto.getFamilyHistory());
        };

        return model;
    }

    public BackgroundHistoryFormDto mapToDto(ClerkBackgroundHistory model){
        BackgroundHistoryFormDto formDto = new BackgroundHistoryFormDto();
        if(ObjectUtils.isNotEmpty(model.getId())){
            formDto.setId(model.getId());
        }
        if(ObjectUtils.isNotEmpty(model.getPresentingComplaint())){
            formDto.setPresentingComplaint(model.getPresentingComplaint());
        }
        if(ObjectUtils.isNotEmpty(model.getHistoryOfPresentingComplaint())){
            formDto.setHistoryOfPresentingComplaint(model.getHistoryOfPresentingComplaint());
        }
        if(ObjectUtils.isNotEmpty(model.getReviewOfSystem())){
            formDto.setReviewOfSystem(model.getReviewOfSystem());
        }
        if(ObjectUtils.isNotEmpty(model.getPastMedicalAndSurgicalHistory())){
            formDto.setPastMedicalAndSurgicalHistory(model.getPastMedicalAndSurgicalHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getPsychiatricHistory())){
            formDto.setPsychiatricHistory(model.getPsychiatricHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getObstetricsAndGynaecologyHistory())){
            formDto.setGynaecologyHistory(model.getObstetricsAndGynaecologyHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getPaediatricHistory())){
            formDto.setPaediatricHistory(model.getPaediatricHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getDrugHistory())){
            formDto.setDrugHistory(model.getDrugHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getImmunizationHistory())){
            formDto.setImmunizationHistory(model.getImmunizationHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getTravelHistory())){
            formDto.setTravelHistory(model.getTravelHistory());
        }
        if(ObjectUtils.isNotEmpty(model.getFamilyHistory())){
            formDto.setFamilyHistory(model.getFamilyHistory());
        }
        return formDto;
    }
}
