package com.hmis.server.hmis.modules.lab.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.lab.dto.*;
import com.hmis.server.hmis.modules.lab.service.LabResultPreparationService;
import com.hmis.server.hmis.modules.lab.service.LabTestRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/lab")
@RequiredArgsConstructor
public class LabSpecimenController {
    private final LabTestRequestService labTestRequestService;

    @GetMapping(value = "/search-lab-test")
    public ResponseEntity<Map<String, Object>> searchLabTestRequest(
            @RequestParam(value = "searchBy") LabSampleSearchedByEnum searchBy,
            @RequestParam(value = "term") String term
    ) {
        return this.labTestRequestService.searchLabTestRequest(searchBy, term);
    }

    @PostMapping(value = "/save-sample-collection")
    public ResponseEntity<MessageDto> saveLabSampleCollection(@RequestBody LabSpecimenCollectionDto dto) {
        return this.labTestRequestService.saveLabSampleCollection(dto);
    }

    @PostMapping(value = "/save-sample-acknowledgement")
    public ResponseEntity<MessageDto> saveLabSampleAcknowledgement(@RequestBody LabSpecimenCollectionDto dto) {
        return this.labTestRequestService.saveLabSampleAcknowledgement(dto);
    }

    @GetMapping(value = "/get-sample-collection")
    public ResponseEntity<LabSpecimenCollectionDto> getLabTestSampleCollection(
            @RequestParam(value = "testId") String testId
    ) {
        return this.labTestRequestService.getLabTestSampleCollection(Long.valueOf(testId));
    }

    @GetMapping(value = "track-test")
    public ResponseEntity<LabTestTrackerDto> trackLabTest(
            @RequestParam(value = "testId") String testId,
            @RequestParam(value = "labBillTestRequestId") String labBillTestRequestId
    ) {
        return this.labTestRequestService.trackLabTest(Long.valueOf(testId), Long.valueOf(labBillTestRequestId));
    }

    public void updateLabSampleCollection() {
    }

    public void saveAcknowledgedSampleCollection() {
    }

    public void updateAcknowledgeSampleCollection() {
    }
}
