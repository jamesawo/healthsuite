package com.hmis.server.hmis.modules.nurse.iservice;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.modules.nurse.dto.VitalSignDto;

public interface IPatientVitalSignService {
	ResponseDto createPatientVitalSign(VitalSignDto dto);

    VitalSignDto findPatientLastCapturedVitalSign(Long patientId);
}
