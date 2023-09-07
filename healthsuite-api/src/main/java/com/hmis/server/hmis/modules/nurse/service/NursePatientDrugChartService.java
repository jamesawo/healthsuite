package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.model.ClerkRequestDrugItem;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.dto.DrugChartTypeEnum;
import com.hmis.server.hmis.modules.nurse.model.NursePatientDrugChart;
import com.hmis.server.hmis.modules.nurse.repository.NursePatientDrugChartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class NursePatientDrugChartService {
    private final NursePatientDrugChartRepository drugChartRepository;

    public NursePatientDrugChartService(
            NursePatientDrugChartRepository drugChartRepository) {
        this.drugChartRepository = drugChartRepository;
    }

    public NursePatientDrugChart saveDrugAdministration(ClerkRequestDrugItem requestDrugItem, PatientDetail patient,
                                                        User user, Department location){
        try {
            NursePatientDrugChart drugChart = new NursePatientDrugChart();
            drugChart.setPatient(patient);
            drugChart.setAdministeredDrugRequestItem(requestDrugItem);
            drugChart.setLocation(location);
            drugChart.setTypeEnum(DrugChartTypeEnum.DRUG_REQUEST);
            drugChart.setUser(user);
            drugChart.setTime(LocalTime.now());
            drugChart.setDate(LocalDate.now());
            return this.drugChartRepository.save(drugChart);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void saveDrugChart(NursePatientDrugChart chart){

    }
}
