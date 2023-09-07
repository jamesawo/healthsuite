package com.hmis.server.hmis.modules.clearking.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.clearking.dto.DoctorWaitingDto;
import com.hmis.server.hmis.modules.clearking.service.DoctorWaitingListServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/doctor-waiting-list" )
public class DoctorWaitingListController {
    @Autowired
    private DoctorWaitingListServiceImpl doctorWaitingListService;

    @GetMapping(value = "all")
    public List<DoctorWaitingDto> getAllDoctorWaiting(){
        return this.doctorWaitingListService.getAllWaitingList();
    }

    @GetMapping(value = "{locationId}")
    public ResponseDto getWaitingList(@PathVariable(value = "locationId") String locationId){
        try{
            ResponseDto responseDto = new ResponseDto();
            List<DoctorWaitingDto> list = this.doctorWaitingListService.getWaitingListByLocation(Long.valueOf(locationId));
            responseDto.setData(list);
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            return responseDto;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "get-attended-by/{doctorId}")
    public List<DoctorWaitingDto> getAttendedList(@PathVariable(value = "doctorId") String doctorId){
        return this.doctorWaitingListService.getAttendedList(Long.valueOf(doctorId));
    }

    @DeleteMapping(value = "remove/{patientId}/{doctorId}")
    public ResponseEntity<Boolean> removeFromWaitingList(@PathVariable(value = "patientId") String patientId, @PathVariable(value = "doctorId") String doctorId){
        return this.doctorWaitingListService.removeFromWaitingList(patientId, doctorId);
    }
}
