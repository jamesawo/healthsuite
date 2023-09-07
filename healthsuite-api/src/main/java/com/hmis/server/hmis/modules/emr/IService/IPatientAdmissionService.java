package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionDto;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPatientAdmissionService {
	ResponseEntity<MessageDto> admitPatient(PatientAdmissionDto dto);
	boolean isPatientOnAdmission(Long patientId);
	PatientAdmissionDto findPatientAdmission(Long patientId);
	PatientAdmission findPatientAdmissionRaw(Long patientId);
	List< PatientDetail > findAllAdmittedPatient();
}
