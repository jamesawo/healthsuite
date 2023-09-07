package com.hmis.server.hmis.modules.nurse.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.nurse.dto.PatientVitalSignSearchDto;
import com.hmis.server.hmis.modules.nurse.dto.VitalSignDto;
import com.hmis.server.hmis.modules.nurse.service.PatientVitalSignServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/nurse-vital-sign")
public class VitalSignCaptureController {

    @Autowired
    private PatientVitalSignServiceImpl signService;


    @PreAuthorize(value = "hasAuthority(@P.NURSES_VITAL_SIGNS_CAPTURE)")
    @PostMapping(value = "capture-vital-sign")
    public ResponseDto saveVitalSigns(@Validated @RequestBody() VitalSignDto dto) {
        return this.signService.createPatientVitalSign(dto);
    }

    @PreAuthorize(value = "hasAuthority(@P.NURSES_VITAL_SIGNS_CAPTURE)")
    @GetMapping(value = "get-vital-sign")
    public ResponseEntity<Boolean> getVitalSigns() {
        return null;
    }


    @GetMapping(value = "get-vital-sign/{patientId}")
    public VitalSignDto viewLastCapturedVital(@PathVariable String patientId) {
        return this.signService.findPatientLastCapturedVitalSign(Long.valueOf(patientId));
    }

    @PostMapping(value = "get-vital-sign-by-date-range")
    public ResponseEntity<?> getVitalSignByDateRange(@RequestBody() PatientVitalSignSearchDto dto) {
        Map<String, List<?>> res = this.signService.findPatientVitalSignByDateRange(dto);
        return ResponseEntity.ok().body(res);
    }
}
