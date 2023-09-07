package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientCategoryService;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryDto;
import com.hmis.server.hmis.modules.emr.model.PatientCategory;
import com.hmis.server.hmis.modules.emr.repository.PatientCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientCategoryServiceImpl implements IPatientCategoryService {

    @Autowired
    private PatientCategoryRepository repository;

    @Override
    public PatientCategory createPatientCategory(PatientCategoryDto patientCategoryDto) {
        PatientCategory patientCategory = new PatientCategory();
        if (patientCategoryDto.getName().isEmpty()){
            throw new HmisApplicationException("provide patient category");
        }else{
            patientCategory.setTitle(patientCategoryDto.getName());
        }

        if (patientCategoryDto.getDescription() != null){
            patientCategory.setDescription(patientCategoryDto.getDescription());
        }

        return this.repository.save(patientCategory);
    }

    @Override
    public PatientCategory updatePatientCategory(PatientCategoryDto patientCategoryDto) {

        if (patientCategoryDto.getId().isPresent()){
            throw new HmisApplicationException("provide patient category id");
        }
        PatientCategory patientCategory = this.findPatientCategory(patientCategoryDto.getId().get());
        if (patientCategoryDto.getName() != null){
            patientCategory.setTitle(patientCategoryDto.getName());
        }
        if (patientCategoryDto.getDescription() != null){
            patientCategory.setDescription(patientCategoryDto.getDescription());
        }

        return this.repository.save(patientCategory);
    }

    @Override
    public PatientCategory findPatientCategory(Long id) {
        Optional<PatientCategory> category = this.repository.findById(id);
        if (!category.isPresent()){
            throw new HmisApplicationException("category with id: "+ id + " not found");
        }
        return category.get();
    }

    @Override
    public void removePatientCategory(Long id) {

    }
}
