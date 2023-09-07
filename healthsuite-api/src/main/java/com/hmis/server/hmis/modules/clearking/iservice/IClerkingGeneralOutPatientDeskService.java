package com.hmis.server.hmis.modules.clearking.iservice;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.modules.clearking.dto.OutPatientDeskDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkingGeneralOutPatientDesk;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface IClerkingGeneralOutPatientDeskService{
    @Transactional
    ResponseDto<String> saveOutPatientDeskAndTemplate(OutPatientDeskDto dto);

    ClerkingGeneralOutPatientDesk saveSession(ClerkingGeneralOutPatientDesk model);

    ResponseEntity<Boolean> saveOutPatientDeskTemplate(OutPatientDeskDto dto);

    OutPatientDeskDto findById(Long id);

    OutPatientDeskDto mapOutPatientDeskModelToDto(ClerkingGeneralOutPatientDesk model);

    ClerkingGeneralOutPatientDesk mapOutPatientDeskDtoToModel(OutPatientDeskDto dto);
}
