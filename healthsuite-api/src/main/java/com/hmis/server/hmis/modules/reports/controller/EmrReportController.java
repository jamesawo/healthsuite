package com.hmis.server.hmis.modules.reports.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.reports.dto.PageResultDto;
import com.hmis.server.hmis.modules.reports.dto.PatientInterimSearchDto;
import com.hmis.server.hmis.modules.reports.dto.PatientRegisterDto;
import com.hmis.server.hmis.modules.reports.dto.SearchAdmissionDto;
import com.hmis.server.hmis.modules.reports.service.EmrReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/report/emr/")
public class EmrReportController {
    @Autowired
    private EmrReportService emrReportService;

    @PostMapping(value = "patient-register")
    public PageResultDto<PatientDetailDto> patientRegisterByDateRange(@RequestBody PatientRegisterDto dto) {
        return this.emrReportService.searchPatientRegister(dto);
    }


    @PostMapping(value = "/patient-register-pdf")
    public ResponseEntity< byte[] > downloadPatientRegisterReport(HttpServletResponse response, @RequestBody PatientRegisterDto dto) throws IOException, JRException {
        try {
            byte[] bytes = this.emrReportService.generatePatientRegisterReportByte(dto);
            return ResponseEntity.ok(bytes);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/search-admission-session")
    public List<SearchAdmissionDto> searchPatientAdmissionSession(@RequestBody PatientInterimSearchDto dto) {
        return this.emrReportService.searchPatientAdmissionSession(dto);
    }

    @PostMapping(value = "/patient-interim-report-pdf")
    public ResponseEntity< byte[] > downloadPatientInterimReport(@RequestBody PatientInterimSearchDto dto)  {
        return this.emrReportService.prepInterimInvoiceMapData(dto);
    }

}
