package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientTransferDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientTransferDetail;

public interface IPatientTransferDetailService {

    PatientTransferDetail createPatientTransferDetail(PatientTransferDetailDto transferDetailDto);

    PatientTransferDetail updatePatientTransferDetail(PatientTransferDetailDto transferDetailDto);

    PatientTransferDetail findPatientTransferDetail(Long id);

    void removePatientTransferDetail(Long id);


}
