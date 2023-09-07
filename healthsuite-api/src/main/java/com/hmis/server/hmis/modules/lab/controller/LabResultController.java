package com.hmis.server.hmis.modules.lab.controller;

import com.hmis.server.hmis.common.common.dto.BatchOrSingleEnum;
import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.lab.dto.LabApproveDto;
import com.hmis.server.hmis.modules.lab.dto.LabResultPrepDto;
import com.hmis.server.hmis.modules.lab.service.LabResultPreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/lab-result")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultPreparationService labResultPreparationService;

    @GetMapping(value = "/get-test/{itemId}/{batchOrSingle}")
    public ResponseEntity<LabResultPrepDto> getLabTestForResultPreparation(
            @PathVariable String itemId,
            @PathVariable BatchOrSingleEnum batchOrSingle) {
        LabResultPrepDto dto = this.labResultPreparationService.getLabTest(Long.valueOf(itemId), batchOrSingle);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/get-test-result/{testId}")
    public ResponseEntity<LabResultPrepDto> getLabTestResult(
            @PathVariable Long testId) {
        LabResultPrepDto dto = this.labResultPreparationService.getLabTestResult(testId);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping(value = "/save-test-result")
    public ResponseEntity<MessageDto> saveLabResultPreparation(@RequestBody LabResultPrepDto dto) {
        return this.labResultPreparationService.saveLabResultPreparation(dto);
    }

    @PostMapping(value = "/approve-test-result")
    public ResponseEntity<MessageDto> approveLabTestResult(@RequestBody LabApproveDto dto) {
        return this.labResultPreparationService.approveLabTestResult(dto);
    }

    @PostMapping(value = "/download-lab-result-pdf")
    public ResponseEntity<byte[]> downloadLabTestResult(@RequestBody LabApproveDto dto) {
        return this.labResultPreparationService.getLabTestResultPdf(dto);
    }

}
