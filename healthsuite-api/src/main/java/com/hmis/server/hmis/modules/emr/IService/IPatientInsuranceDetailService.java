package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientInsuranceDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientInsuranceDetail;

public interface IPatientInsuranceDetailService {

    PatientInsuranceDetail createPatientInsuranceDetail(PatientInsuranceDetailDto patientInsuranceDetailDto);

    PatientInsuranceDetail updatePatientInsuranceDetail(PatientInsuranceDetailDto insuranceDetailDto);

    PatientInsuranceDetail findPatientInsuranceDetail(Long id);

    void removePatientInsuranceDetail(Long id);

}
