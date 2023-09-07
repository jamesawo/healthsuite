package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientMeansOfIdentificationDto;
import com.hmis.server.hmis.modules.emr.model.PatientMeansOfIdentification;

public interface IPatientMeansOfIdentificationService {

    PatientMeansOfIdentification createPatientMeansOfIdentification(PatientMeansOfIdentificationDto meansOfIdentificationDto);

    PatientMeansOfIdentification updatePatientMeansOfIdentification(PatientMeansOfIdentificationDto meansOfIdentificationDto);

    PatientMeansOfIdentification findPatientMeansOfIdentification(Long id);

    void removePatientMeansOfIdentification(Long id);
}
