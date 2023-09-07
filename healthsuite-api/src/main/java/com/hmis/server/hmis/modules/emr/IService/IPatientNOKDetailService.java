package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientNOKDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientNOKDetail;

public interface IPatientNOKDetailService {

    PatientNOKDetail createPatientNOKDetail(PatientNOKDetailDto nokDetailDto);

    PatientNOKDetail updatePatientNOKDetail(PatientNOKDetailDto nokDetailDto);

    PatientNOKDetail findPatientNOKDetail(Long id);

    void removePatientNOKDetail(Long id);
}
