package com.hmis.server.hmis.modules.clearking.iservice;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkDoctorRequestDto;
import com.hmis.server.hmis.modules.clearking.model.*;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;

import java.time.LocalDate;
import java.util.List;

public interface IDoctorRequestService {
//    List<ClerkRequestDrug> getDoctorDrugRequest(Long patientId);

    List<ClerkRequestDrug> getDoctorDrugRequest(PatientDetail patient, LocalDate startDate, LocalDate endDate);

    List<ClerkRequestDrug> searchDrugRequestByDateRange(Long patientId, DateDto start, DateDto end);

    void saveDoctorRequest(ClerkDoctorRequestDto dto);

    // save drug request
    ClerkRequestDrug saveDrugRequest(ClerkRequestDrug requestDrug, PatientDetail patient);

    // save drug items in drug request
    void saveDrugItemsRequest(ClerkRequestDrug requestDrug, List<ClerkRequestDrugItem> items);

    // save lab request
    ClerkRequestLab saveLabRequest(ClerkRequestLab requestLab, PatientDetail patient);

    // save lab items in lab request
    void saveLabItemsRequest(ClerkRequestLab lab, List<ClerkRequestLabItems> items);

    // save radiology request
    ClerkRequestRadiology saveRadiologyRequest(ClerkRequestRadiology requestRadiology, PatientDetail patient);
    // save radiology items in radiology request
    void saveRadiologyItemsRequest(ClerkRequestRadiology radiology, List<ClerkRequestRadiologyItems> items);

    // get doctor lab request
    List<ClerkRequestLab> getDoctorLabRequest(PatientDetail patientDetail, LocalDate startDate, LocalDate endDate);

    List<ClerkRequestRadiology> searchRadiologyRequestByDataRange(Long patientId, DateDto startDate, DateDto endDate);

    List<ClerkRequestLab> searchLabRequestByDataRange(Long patientId, DateDto startDate, DateDto endDate);

    List<ClerkRequestRadiology> getDoctorRadiologyRequest(PatientDetail patientDetail, LocalDate startDate, LocalDate endDate);

    ClerkDoctorRequest findById(Long id);
}
