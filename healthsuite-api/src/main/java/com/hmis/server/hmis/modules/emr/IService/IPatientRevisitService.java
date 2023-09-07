package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientRevisitDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientVisitHistory;
import java.time.LocalDate;
import java.util.List;

public interface IPatientRevisitService {

	boolean getPatientRevisitStatus(Long patientId);

	boolean isPatientVisitActive(Long patientId);

	PatientRevisitDto revisitAPatient(PatientRevisitDto dto);

	List< PatientVisitHistory > findAllVisit();

	List< PatientVisitHistory > findAllVisitWithinDate(LocalDate startDate, LocalDate endDate);

    void runPatientRevisitStatusScheduler();

    PatientRevisitDto createRevisitRecord(PatientRevisitDto revisitDto);

	PatientVisitHistory createRevisitHistory(PatientVisitHistory model);

	boolean prepPatientAutoRevisit(PatientDetail patient, Long clinicId);

	PatientRevisitDto getPatientActiveVisitRecord(Long patientId);

	void setPatientVisitRecordAfterRegistration(Long patientId, Long outletId);
}
