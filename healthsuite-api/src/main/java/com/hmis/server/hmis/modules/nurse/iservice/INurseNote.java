package com.hmis.server.hmis.modules.nurse.iservice;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.modules.nurse.dto.NurseNoteDto;

public interface INurseNote {
	ResponseDto createOne(NurseNoteDto dto);
}
