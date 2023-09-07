package com.hmis.server.hmis.modules.clearking.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.clearking.dto.GeneralClerkDeskDto;
import com.hmis.server.hmis.modules.clearking.dto.OutPatientDeskDto;
import com.hmis.server.hmis.modules.clearking.service.ClerkingGeneralOutPatientDeskServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/clerk-consultation" )
public class ClerkPatientConsultationController {
    private final ClerkingGeneralOutPatientDeskServiceImpl deskService;

    public ClerkPatientConsultationController(
            ClerkingGeneralOutPatientDeskServiceImpl deskService) {
        this.deskService = deskService;
    }

    @PostMapping(value = "save-general-out-patient-template-only")
    public ResponseEntity<Boolean> saveTemplateOnly(@RequestBody OutPatientDeskDto template){
        return this.deskService.saveOutPatientDeskTemplate(template);
    }

    @PostMapping(value = "save-general-out-patient-desk")
    public ResponseDto<String> saveOutPatientDesk(@RequestBody OutPatientDeskDto dto){
        return this.deskService.saveOutPatientDeskAndTemplate(dto);
    }

    @PostMapping(value = "save-general-clerk-desk")
    public ResponseEntity<MessageDto> saveGeneralClerkingDesk(@RequestBody GeneralClerkDeskDto dto){
        return this.deskService.saveGeneralClerkingDesk(dto);
    }


}
