package com.hmis.server.hmis.modules.reports.iservice;

import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.reports.dto.PageResultDto;
import com.hmis.server.hmis.modules.reports.dto.PatientRegisterDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IEmrReportService {
//    List<PatientRegisterDto> findPatientRegister(PatientRegisterDto dto);

    PageResultDto<PatientDetailDto> searchPatientRegister(PatientRegisterDto dto);
}
