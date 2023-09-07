package com.hmis.server.hmis.modules.nurse.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrugItem;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.nurse.dto.NurseObstetricsHistoryDto;
import com.hmis.server.hmis.modules.nurse.dto.PatientCardNoteDto;
import com.hmis.server.hmis.modules.nurse.dto.PatientFluidBalanceDto;
import com.hmis.server.hmis.modules.nurse.dto.PatientIcuBounceDto;
import com.hmis.server.hmis.modules.nurse.service.NurseDrugAdministrationService;
import com.hmis.server.hmis.modules.nurse.service.NursePatientCardNoteServiceImpl;
import com.hmis.server.hmis.modules.nurse.service.NursePatientFluidService;
import com.hmis.server.hmis.modules.nurse.service.NursePatientIcuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/nurse")
public class NurseGeneralController {

    private final NursePatientCardNoteServiceImpl cardNoteService;
    private final NurseDrugAdministrationService drugAdministrationService;
    private final NursePatientFluidService nursePatientFluidService;
    private final NursePatientIcuService icuService;

    @Autowired
    public NurseGeneralController(
            NursePatientCardNoteServiceImpl cardNoteService,
            NurseDrugAdministrationService drugAdministrationService, NursePatientFluidService nursePatientFluidService, NursePatientIcuService icuService) {
        this.cardNoteService = cardNoteService;
        this.drugAdministrationService = drugAdministrationService;
        this.nursePatientFluidService = nursePatientFluidService;
        this.icuService = icuService;
    }

    @PostMapping(value = "download-patient-e-folder-pdf")
    public ResponseEntity<byte[]> downloadPatientCardNote(@RequestBody PatientCardNoteDto dto){
        return this.cardNoteService.downloadPatientEFolder(dto);
    }

    @PostMapping(value = "download-patient-anc-card-note-pdf")
    public ResponseEntity<byte[]> downloadPatientAncCardNote(@RequestBody PatientCardNoteDto dto){
        return this.cardNoteService.downloadPatientAncCardNote(dto);
    }

    @PostMapping(value = "save-obstetrics-history")
    public ResponseEntity<MessageDto> savePatientObstetricsHistory(@RequestBody NurseObstetricsHistoryDto dto){
        return this.cardNoteService.savePatientObstetricsHistory(dto);
    }

    @PostMapping(value = "save-drug-administration")
    public ResponseEntity<MessageDto> saveDrugAdministration(@RequestBody ClerkRequestDrugItem dto) {
        return this.drugAdministrationService.saveDrugAdministration(dto);
    }

    @PostMapping(value = "patient-fluid-balance-save")
    public ResponseEntity<MessageDto> savePatientFluidBalance(@RequestBody PatientFluidBalanceDto dto){
        return this.nursePatientFluidService.savePatientFluidBalance(dto);
    }

    @PostMapping(value = "patient-fluid-balance-get-previous")
    public ResponseEntity<byte[]> getPatientFluidBalance(@RequestBody PatientDetailDto dto){
        return this.nursePatientFluidService.getPatientFluidBalance(dto);
    }

    @PostMapping(value = "save-icu-bounce-back")
    public ResponseEntity<MessageDto> saveIcuBounceBack(@RequestBody PatientIcuBounceDto dto) {
        return this.icuService.savePatientIcuBounceBack(dto);
    }
}
