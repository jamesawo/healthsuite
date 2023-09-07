package com.hmis.server.hmis.modules.clearking.iservice;

import com.hmis.server.hmis.modules.clearking.dto.ClerkingTemplateDto;
import com.hmis.server.hmis.modules.clearking.dto.ClinicDeskEnum;
import com.hmis.server.hmis.modules.clearking.dto.OutPatientDeskDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkingDoctorTemplate;
import com.hmis.server.hmis.modules.clearking.model.ClerkingGeneralOutPatientDesk;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IClerkingTemplateService {
    ResponseEntity<Boolean> saveGeneralOutPatientDeskTemplate(ClerkingGeneralOutPatientDesk desk, String templateName);

    List<ClerkingTemplateDto> searchDoctorTemplate(String term, Long doctorId, ClinicDeskEnum deskEnum);

    ClerkingDoctorTemplate findOne(Long id);

    OutPatientDeskDto findOutPatientDeskTemplate(Long templateId);
}
