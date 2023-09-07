package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.dto.LabParameterRangeSetupItemDto;
import com.hmis.server.hmis.modules.lab.dto.LabResultPrepDto;
import com.hmis.server.hmis.modules.lab.dto.LabResultTestParamDto;
import com.hmis.server.hmis.modules.lab.model.*;
import com.hmis.server.hmis.modules.lab.repository.LabTestResultItemRepository;
import com.hmis.server.hmis.modules.lab.repository.LabTestResultRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.constant.HmisConstant.NIL;

@Service
public class LabTestResultService {
    private final LabTestResultRepository labTestResultRepository;
    private final LabTestResultItemRepository labTestResultItemRepository;
    private final LabTestRequestService labTestRequestService;


    public LabTestResultService(
            LabTestResultRepository labTestResultRepository,
            LabTestResultItemRepository labTestResultItemRepository,
            @Lazy LabTestRequestService labTestRequestService
    ) {
        this.labTestResultRepository = labTestResultRepository;
        this.labTestResultItemRepository = labTestResultItemRepository;
        this.labTestRequestService = labTestRequestService;
    }

    public void updateLabTestResultValues(LabTestPreparation preparation, LabResultPrepDto dto) {
        List<LabResultTestParamDto> testParameterList = dto.getTestParameterList();
        if (testParameterList != null && !testParameterList.isEmpty()) {
            for (LabResultTestParamDto testParamDto : testParameterList) {
                // save test result;
                LabTestRequestItem requestItem = this.labTestRequestService.findOneLabTestItem(testParamDto.getTestParamId());
                String reportFilm = testParamDto.getFilmReport() != null ? testParamDto.getFilmReport() : NIL;
                String comment = testParamDto.getComment() != null ? testParamDto.getComment() : NIL;

                requestItem.setComment(comment);
                requestItem.setFilmLabel(reportFilm);

                LabTestResult result = this.saveLabTestResult(comment, reportFilm, preparation.getPatientDetail(), requestItem, preparation);
                // save test result item
                List<LabParameterRangeSetupItemDto> rangeParamList = testParamDto.getTestRangeParamList();
                if (rangeParamList != null && !rangeParamList.isEmpty()){
                    for (LabParameterRangeSetupItemDto rangeItem : rangeParamList) {
                        Long rangeItemId = rangeItem.getId();
                        Double value = rangeItem.getValue();
                        this.saveLabTestResultItem(result, value, new LabParameterRangeSetupItem(rangeItemId));
                    }
                }
            }
        }
    }

    public LabTestResult findTestResult(PatientDetail patient, LabTestPreparation preparation, LabTestRequestItem item) {
        Optional<LabTestResult> optional = this.labTestResultRepository.findByPatientAndTestPreparationAndTestRequestItem(patient, preparation, item);
        return optional.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find lab test result"));
    }

    public LabTestResultItem findTestResultItem(LabTestResult labTestResult, LabParameterRangeSetupItem rangeSetupItem){
        Optional<LabTestResultItem> optional = this.labTestResultItemRepository.findByLabTestResultAndRangeSetupItem(labTestResult, rangeSetupItem);
        return optional.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "cannot find lab test result item"));
    }

    /* save result wrapper */
    public LabTestResult saveLabTestResult(
            String comment,
            String reportFilm,
            PatientDetail patient,
            LabTestRequestItem testRequestItem, LabTestPreparation testPreparation
    ) {
        LabTestResult result = new LabTestResult();
        result.setTestRequestItem(testRequestItem);
        result.setTestPreparation(testPreparation);
        result.setPatient(patient);
        Optional<LabTestResult> optionalLabTestResult = this.labTestResultRepository.findByTestPreparationAndTestRequestItem(testPreparation, testRequestItem);
        optionalLabTestResult.ifPresent(labTestResult -> result.setId(labTestResult.getId()));
        return this.labTestResultRepository.save(result);
    }

    /* save result item values */
    public void saveLabTestResultItem(
            LabTestResult testResult, Double testValue, LabParameterRangeSetupItem rangeSetupItem
    ) {
        LabTestResultItem resultItem = new LabTestResultItem();
        resultItem.setLabTestResult(testResult);
        resultItem.setTestValue(testValue);
        resultItem.setRangeSetupItem(rangeSetupItem);
        Optional<LabTestResultItem> optional = this.labTestResultItemRepository.findByLabTestResultAndRangeSetupItem(testResult, rangeSetupItem);
        optional.ifPresent(value -> resultItem.setId(value.getId()));
        this.labTestResultItemRepository.save(resultItem);
    }
}
