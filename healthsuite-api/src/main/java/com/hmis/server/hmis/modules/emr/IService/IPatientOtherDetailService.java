package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientOtherDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientOtherDetail;


public interface IPatientOtherDetailService {

    PatientOtherDetail createPatientOtherDetail(PatientOtherDetailDto otherDetailDto);

    PatientOtherDetail updatePatientOtherDetail(PatientOtherDetailDto otherDetailDto);

    PatientOtherDetail findPatientOtherDetail(Long id);

    void removePatientOtherDetail(Long id);


}
