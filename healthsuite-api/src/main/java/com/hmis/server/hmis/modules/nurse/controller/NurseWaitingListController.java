package com.hmis.server.hmis.modules.nurse.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.nurse.dto.AttendedDto;
import com.hmis.server.hmis.modules.nurse.dto.NurseWaitingDto;
import com.hmis.server.hmis.modules.nurse.dto.NurseWaitingHistoryDto;
import com.hmis.server.hmis.modules.nurse.service.NurseWaitingListServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/patient-waiting-list" )
public class NurseWaitingListController {
	@Autowired
	private NurseWaitingListServiceImpl waitingListService;

	@GetMapping( value = "nurse-list" )
	public List< NurseWaitingDto > getAllActiveWaitingList(){
		return this.waitingListService.getActiveWaitingList();
	}

	@PostMapping( value = "get-attended-list" )
	public List< NurseWaitingDto > getAllAttendedList(@RequestBody AttendedDto attendedDto) {
		return this.waitingListService.getNurseAttendedList(attendedDto);
	}


	@PatchMapping( value = "remove-patient" )
	public ResponseDto removePatientFromWaitingList(@RequestBody NurseWaitingHistoryDto dto) {
		try {
			return this.waitingListService.removePatientFromWaitingList(dto);
		}
		catch( Exception e ) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
