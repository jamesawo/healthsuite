package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientContactDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientContactDetail;

public interface IPatientContactDetailService {

    PatientContactDetail createPatientContactDetail(PatientContactDetailDto contactDetailDto);

    PatientContactDetail updatePatientContactDetail(PatientContactDetailDto contactDetailDto);

    PatientContactDetail findPatientContactDetail(Long id);

    void removePatientContactDetail(Long id);
}
