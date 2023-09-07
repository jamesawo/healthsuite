package com.hmis.server.hmis.modules.billing.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.billing.dto.PatientBillDto;
import com.hmis.server.hmis.modules.billing.service.PatientBillMobileApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( HmisConstant.API_PREFIX +"/mobile")
@RequiredArgsConstructor
public class PatientBillMobileApiController {
    private final PatientBillMobileApiService apiService;

    @GetMapping(value = "/bill/{invoiceNumber}")
    public ResponseEntity<PatientBillDto> searchBillByMobileApi(
            @RequestHeader(value = "Authorization") String basicAuth,
            @PathVariable String invoiceNumber){
       return this.apiService.findBillForMobileApi(invoiceNumber, basicAuth);
    }

    @PostMapping(value = "/bill/pay/{invoiceNumber}")
    public ResponseEntity<Boolean> payBillByMobileApi(
            @RequestHeader(value = "Authorization") String basicAuth,
            @PathVariable String invoiceNumber){
        return this.apiService.setBillIsPaidByMobileApi(invoiceNumber, basicAuth);
    }
}
