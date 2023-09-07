package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientCategoryDto;
import com.hmis.server.hmis.modules.emr.model.PatientCategory;

public interface IPatientCategoryService {

    PatientCategory createPatientCategory(PatientCategoryDto patientCategoryDto);

    PatientCategory updatePatientCategory( PatientCategoryDto patientCategoryDto );

    PatientCategory findPatientCategory(Long id);

    void removePatientCategory(Long id);

}
