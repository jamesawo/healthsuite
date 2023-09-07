package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;

import java.util.List;
import java.util.Map;

public interface IPatientDetailService {

    PatientDetail updatePatientDetail(PatientDetailDto detailDto);

    PatientDetail findPatientDetailById(Long id);

    PatientDetail findPatientDetailByName(String name);

    PatientDetailDto createPatient(PatientDetailDto patientDetailDto);

    void removePatientDetail(Long id);

    Map<String, String> getPatientRegistrationGlobalSetting();

    void updatePatientRevisitStatus(Long patientId, boolean status);

    // void setPatientActiveVisit(Long patientId, Long clinicId);

    Scheme findPatientScheme(Long patientId);

    Scheme findPatientScheme(PatientDetail patientDetail);

    String findPatientSchemeApprovalCode(PatientDetail patient);

    String findPatientDiagnosis(PatientDetail patient);

    String findPatientSchemeCode(PatientDetail patient);

    PatientDetailDto mapToPatientDto(PatientDetail patientDetail);
}
