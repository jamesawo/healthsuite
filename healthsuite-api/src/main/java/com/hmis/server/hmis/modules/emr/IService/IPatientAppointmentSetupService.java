package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientAppointmentSetupDto;

public interface IPatientAppointmentSetupService {
	PatientAppointmentSetupDto createAppointmentSetup(PatientAppointmentSetupDto dto);

	PatientAppointmentSetupDto updateAppointmentSetup(PatientAppointmentSetupDto dto);

	int getConsultantLimitCount( Long consultantId );

	boolean existByConsultantAndSpeciality( Long consultantId, Long specialityId );
}
