package com.hmis.server.hmis.modules.clearking.iservice;

import com.hmis.server.hmis.modules.clearking.dto.DoctorWaitingDto;
import com.hmis.server.hmis.modules.clearking.dto.DoctorListWrapper;
import com.hmis.server.hmis.modules.clearking.model.DoctorWaitingList;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDoctorWaitingListService {
    void addToWaitingList(DoctorListWrapper dto);

    void notifyDoctorWaitingSockListener(DoctorWaitingList waitingList);

    List<DoctorWaitingDto> getWaitingListByLocation(Long locationId);

    List<DoctorWaitingDto> getAllWaitingList();

    List<DoctorWaitingDto> getAttendedList(Long doctorId);

    void runClearWaitingListScheduler();

    ResponseEntity<Boolean> removeFromWaitingList(String patientId, String docId);
}
