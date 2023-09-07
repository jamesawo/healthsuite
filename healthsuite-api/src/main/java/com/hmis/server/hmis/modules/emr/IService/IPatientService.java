package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.modules.emr.dto.*;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPatientService {

	PatientDetailDto patientRegistration(PatientDetailDto patientDetailDto);

	ResponseDto patientRecordUpdate(PatientDetailDto patientDetailDto);

	//List<PatientDetailDto> searchPatientRecord(String searchParam);

	List< PatientDetailDto > patientRecordTypeaheadSearch(
			String searchParam,
			boolean checkAdmission,
			boolean loadAdmission,
			boolean loadRevisit,
			boolean loadSchemeDiscount,
			boolean loadDeposit,
			boolean loadDrugRequest,
			boolean loadLabRequest,
			boolean loadRadiologyRequest
			);

	ResponseDto patientRevisit(PatientRevisitDto dto);

	ResponseEntity<MessageDto> patientAdmission(PatientAdmissionDto admissionDto);

	PatientAppointmentSetupDto createAppointmentSetup(PatientAppointmentSetupDto dto);

	List< PatientAppointmentDto > findPatientOpenAppointment(Long patientId);

	PatientAppointmentDto createBooking(PatientAppointmentDto dto);

	ResponseDto< Boolean > cancelPatientAppointment(Long appontmentId);

	String getPatientFullName(PatientDetail patient);

	String getPatientFullName(Long patientId);

	boolean isSchemePatient(PatientDetail patientDetail);

	PatientDetailDto findPatientById(Long patientId);
}
