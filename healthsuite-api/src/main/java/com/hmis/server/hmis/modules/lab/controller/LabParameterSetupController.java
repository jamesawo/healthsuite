package com.hmis.server.hmis.modules.lab.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.lab.dto.LabParameterRangeSetupDto;
import com.hmis.server.hmis.modules.lab.dto.LabParameterSetupDto;
import com.hmis.server.hmis.modules.lab.service.LabParameterRangeSetupService;
import com.hmis.server.hmis.modules.lab.service.LabParameterSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController @RequiredArgsConstructor
@RequestMapping(HmisConstant.API_PREFIX +"/lab-parameter-setup")
public class LabParameterSetupController {
    private final LabParameterSetupService labParameterSetupService;
    private final LabParameterRangeSetupService rangeSetupService;

    @PostMapping(value = "save-setup")
    public ResponseEntity<MessageDto> saveLabParameterSetup(@RequestBody LabParameterSetupDto dto) {
        return this.labParameterSetupService.saveLabParameterSetup(dto);
    }

    @GetMapping(value = "search-setup-by-test/{testId}")
    public ResponseEntity<LabParameterSetupDto> searchParameterByTestId(
            @PathVariable String testId) {
        return ResponseEntity.ok().body(this.labParameterSetupService.searchByTest(Long.valueOf(testId)));
    }

    @PostMapping(value = "save-range-setup")
    public ResponseEntity<MessageDto> saveLabParameterRangeSetup(@RequestBody LabParameterRangeSetupDto dto) {
        return this.rangeSetupService.saveParameterRangeSetup(dto);
    }

    @GetMapping(value = "search-range-by-test-param/{testId}/{paramId}")
    public ResponseEntity<LabParameterRangeSetupDto> searchRangeByTestAndParameter(
            @PathVariable String paramId, @PathVariable String testId) {
        LabParameterRangeSetupDto list = this.rangeSetupService.searchRangeByTestAndParameter(Long.valueOf(testId), Long.valueOf(paramId));
        return ResponseEntity.ok().body(list);
    }

}
