package com.hmis.server.hmis.modules.emr.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.emr.dto.PatientReferralDto;
import com.hmis.server.hmis.modules.emr.service.PatientTransferDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HmisConstant.API_PREFIX +"/clinic-referral")
public class PatientClinicReferralController {

    private final PatientTransferDetailServiceImpl transferDetailService;

    @Autowired
    public PatientClinicReferralController(PatientTransferDetailServiceImpl transferDetailService) {
        this.transferDetailService = transferDetailService;
    }

    @PostMapping(value = "refer-to-clinic")
    public ResponseEntity<MessageDto> referPatientToNewClinic(@RequestBody PatientReferralDto dto) {
        return this.transferDetailService.referPatientToClinic(dto);
    }
}
