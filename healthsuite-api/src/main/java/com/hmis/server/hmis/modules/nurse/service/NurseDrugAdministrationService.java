package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrugItem;
import com.hmis.server.hmis.modules.clearking.service.DoctorRequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NurseDrugAdministrationService {
    private final DoctorRequestServiceImpl doctorRequestService;

    @Autowired
    public NurseDrugAdministrationService(DoctorRequestServiceImpl doctorRequestService) {
        this.doctorRequestService = doctorRequestService;
    }


    public ResponseEntity<MessageDto> saveDrugAdministration(ClerkRequestDrugItem drugItem) {
        Optional<String> comment = Optional.ofNullable(drugItem.getNurseComment());
        Long user = drugItem.getUserId();
        Long location = drugItem.getLocationId();
        Long patient = drugItem.getPatientId();
        Long requestItemId = drugItem.getId();
        this.doctorRequestService.updateDrugRequestItem(comment, user, location, patient, requestItemId);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("Drug Administration Successful."));
    }
}
