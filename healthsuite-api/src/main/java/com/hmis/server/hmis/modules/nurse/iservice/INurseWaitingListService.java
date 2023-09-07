package com.hmis.server.hmis.modules.nurse.iservice;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.dto.AttendedDto;
import com.hmis.server.hmis.modules.nurse.dto.NurseWaitingDto;
import com.hmis.server.hmis.modules.nurse.dto.NurseWaitingHistoryDto;
import com.hmis.server.hmis.modules.nurse.model.NurseWaitingList;
import java.util.List;
import java.util.Optional;

public interface INurseWaitingListService {
	Optional< NurseWaitingList > findOneByPatientId(Long patientId);

	void addPatientToWaitingList(PatientDetail patientDetail, List< Long > clinicIds);

	List< NurseWaitingDto > getActiveWaitingList();

	ResponseDto removePatientFromWaitingList(NurseWaitingHistoryDto waitingHistoryDto);

	List< NurseWaitingDto > getNurseAttendedList(AttendedDto attendedDto);
}
