package com.hmis.server.hmis.modules.nurse.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.nurse.dto.NurseNoteDto;
import com.hmis.server.hmis.modules.nurse.service.NurseNoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/nurse-note" )
public class NurseNoteController {

	@Autowired
	private NurseNoteServiceImpl service;

	@PostMapping(value="create")
	public ResponseDto createOne(@RequestBody() NurseNoteDto dto) {
		return this.service.createOne(dto);
	}
	

}
