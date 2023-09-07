package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.ClinicalAssessmentFormDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkClinicalAssessment;
import com.hmis.server.hmis.modules.clearking.repository.PatientClinicalAssessmentRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientClinicalAssessmentServiceImpl {
    @Autowired
    private PatientClinicalAssessmentRepository repository;

    public ClerkClinicalAssessment findById(Long id) {
        Optional<ClerkClinicalAssessment> optional = this.repository.findById(id);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    public ClerkClinicalAssessment save(ClerkClinicalAssessment model) {
        try {
            return this.repository.save(model);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ClerkClinicalAssessment save(ClinicalAssessmentFormDto dto) {
        ClerkClinicalAssessment model = this.mapToModel(dto);
        return this.save(model);
    }

    public ClerkClinicalAssessment mapToModel(ClinicalAssessmentFormDto dto) {
        ClerkClinicalAssessment model = new ClerkClinicalAssessment();
        if (ObjectUtils.isNotEmpty(dto.getId())) {
            model.setId(dto.getId());
        }

        if (ObjectUtils.isNotEmpty(dto.getClinicalAssessment())) {
            model.setClinicalAssessment(dto.getClinicalAssessment());
        }
        if (ObjectUtils.isNotEmpty(dto.getProvisionalDiagnosis())) {
            model.setProvisionalDiagnosis(dto.getProvisionalDiagnosis());
        }
        if (ObjectUtils.isNotEmpty(dto.getTreatmentPlan())) {
            model.setTreatmentPlan(dto.getTreatmentPlan());
        }
        if (ObjectUtils.isNotEmpty(dto.getRecordInvestigationResults())) {
            model.setRecordInvestigationResults(dto.getRecordInvestigationResults());
        }
        if (ObjectUtils.isNotEmpty(dto.getFollowUpNote())) {
            model.setFollowUpNote(dto.getFollowUpNote());
        }
        return model;
    }

    public ClinicalAssessmentFormDto mapToDto(ClerkClinicalAssessment model) {
        ClinicalAssessmentFormDto formDto = new ClinicalAssessmentFormDto();
        if (ObjectUtils.isNotEmpty(model.getId())) {
            formDto.setId(model.getId());
        }

        if (ObjectUtils.isNotEmpty(model.getClinicalAssessment())) {
            formDto.setClinicalAssessment(model.getClinicalAssessment());
        }
        if (ObjectUtils.isNotEmpty(model.getProvisionalDiagnosis())) {
            formDto.setProvisionalDiagnosis(model.getProvisionalDiagnosis());
        }
        if (ObjectUtils.isNotEmpty(model.getTreatmentPlan())) {
            formDto.setTreatmentPlan(model.getTreatmentPlan());
        }
        if (ObjectUtils.isNotEmpty(model.getRecordInvestigationResults())) {
            formDto.setRecordInvestigationResults(model.getRecordInvestigationResults());
        }
        if (ObjectUtils.isNotEmpty(model.getFollowUpNote())) {
            formDto.setFollowUpNote(model.getFollowUpNote());
        }
        return formDto;
    }
}
