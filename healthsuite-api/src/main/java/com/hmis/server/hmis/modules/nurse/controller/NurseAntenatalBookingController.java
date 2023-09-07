package com.hmis.server.hmis.modules.nurse.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.nurse.dto.PatientAntenatalDto;
import com.hmis.server.hmis.modules.nurse.service.PatientAntenatalBookingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/antenatal-booking" )
public class NurseAntenatalBookingController {

	@Autowired
	private PatientAntenatalBookingServiceImpl antenatalBookingService;

	@PostMapping(value = "create")
	public ResponseDto createBooking( @RequestBody PatientAntenatalDto dto){
		return this.antenatalBookingService.saveBooking(dto);
	}

}
