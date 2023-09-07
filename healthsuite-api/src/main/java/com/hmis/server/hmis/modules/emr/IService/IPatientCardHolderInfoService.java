package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientCardHolderInfoDto;
import com.hmis.server.hmis.modules.emr.model.PatientCardHolderInfo;

public interface IPatientCardHolderInfoService {

    PatientCardHolderInfo createPatientCardHolderInfo(PatientCardHolderInfoDto patientCardHolderInfoDto);

    PatientCardHolderInfo updatePatientCardHolderInfo(PatientCardHolderInfoDto patientCardHolderInfoDto);

    PatientCardHolderInfo findPatientCardHolderInfo(Long id);

    void removePatientCardHolderInfo(Long id);
}
