package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.LabBillTestItemsDto;
import com.hmis.server.hmis.modules.lab.dto.LabBillTestRequestDto;
import com.hmis.server.hmis.modules.lab.dto.LabSpecimenCollectionDto;
import com.hmis.server.hmis.modules.lab.model.LabTestRequest;
import com.hmis.server.hmis.modules.lab.model.LabSpecimenCollection;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import com.hmis.server.hmis.modules.lab.repository.LabSpecimenCollectionRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LabSpecimenCollectionService {
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;
    private final LabSpecimenCollectionRepository specimenCollectionRepository;


    public LabSpecimenCollectionService(
            UserServiceImpl userService,
            DepartmentServiceImpl departmentService,
            LabSpecimenCollectionRepository specimenCollectionRepository
    ) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.specimenCollectionRepository = specimenCollectionRepository;
    }


    public LabSpecimenCollection saveSpecimenCollection(LabSpecimenCollection collection) {
        return this.specimenCollectionRepository.save(collection);
    }

    public void updateSpecimenCollectionWithTestItem(LabSpecimenCollection collection, LabTestRequestItem testItem) {
        if (collection.getId() != null) {
            collection.setLabTestRequestItem(testItem);
            this.saveSpecimenCollection(collection);
        }
    }

    public Optional<LabSpecimenCollection> findLabSampleCollectionByLabTestRequest(Long testRequestId){
        return this.specimenCollectionRepository.findByLabTestRequest_Id(testRequestId);
    }

    public LabSpecimenCollectionDto getLabTestSearchSampleCollection(Long labTestRequestId) {
        Optional<LabSpecimenCollection> optional = this.findLabSampleCollectionByLabTestRequest(labTestRequestId);
        if (optional.isPresent()){
            return this.mapLabSpecimenModelToDto(optional.get());
        }
        return new LabSpecimenCollectionDto();
    }

    public LabSpecimenCollectionDto getLabTestSearchSampleCollectionClean(Long labTestRequestId) {
        Optional<LabSpecimenCollection> optional = this.findLabSampleCollectionByLabTestRequest(labTestRequestId);
        LabSpecimenCollectionDto dto = new LabSpecimenCollectionDto();
        if (optional.isPresent()){
            LabSpecimenCollection model = optional.get();
            this.setLabSpecimenCollectionDtoPre(dto, model);
            return dto;
        }
        return dto;
    }

    public LabSpecimenCollection findByLabTestRequestId(Long testReqId) {
        Optional<LabSpecimenCollection> optional = this.findLabSampleCollectionByLabTestRequest(testReqId);
        return optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public LabSpecimenCollectionDto findByLabTestReqToDto(LabTestRequest billTestRequest) {
        LabSpecimenCollection labSpecimenCollection = this.findByLabTestRequestId(billTestRequest.getId());
        return this.mapLabSpecimenModelToDto(labSpecimenCollection);
    }

    /* get lab test collected samples for acknowledgment */
    public List<LabSpecimenCollectionDto> getLabTestSampleCollection(List<LabTestRequest> testList) {
        List<LabSpecimenCollectionDto> dtoList = new ArrayList<>();
        if (!testList.isEmpty()) {
            for (LabTestRequest test : testList) {
                Optional<LabSpecimenCollection> optional = this.specimenCollectionRepository.findByLabTestRequest_Id(test.getId());
                LabSpecimenCollection collection = this.findByLabTestRequestId(test.getId());
                dtoList.add(this.mapLabSpecimenModelToDto(collection));
            }
        }
        return dtoList;
    }


    public LabSpecimenCollection mapLabSpecimenCollectionToModel(LabSpecimenCollectionDto dto) {
        LabSpecimenCollection model = new LabSpecimenCollection();
        if (ObjectUtils.isNotEmpty(dto.getNewOrEditSampleEnum())) {
            model.setNewOrEditSampleEnum(dto.getNewOrEditSampleEnum());
        }

        if (ObjectUtils.isNotEmpty(dto.getSearchByEnum())) {
            model.setSearchByEnum(dto.getSearchByEnum());
        }

        if (ObjectUtils.isNotEmpty(dto.getProvisionalDiagnosis())) {
            model.setProvisionalDiagnosis(dto.getProvisionalDiagnosis());
        }
        if (ObjectUtils.isNotEmpty(dto.getOtherInformation())) {
            model.setOtherInformation(dto.getOtherInformation());
        }

        if (ObjectUtils.isNotEmpty(dto.getClinicalSummary())) {
            model.setClinicalSummary(dto.getClinicalSummary());
        }

        if (ObjectUtils.isNotEmpty(dto.getCapturedBy()) && dto.getCapturedBy().getId().isPresent()) {
            model.setCapturedBy(this.userService.findOneRaw(dto.getCapturedBy().getId().get()));
        }

        if (ObjectUtils.isNotEmpty(dto.getCapturedFrom()) && dto.getCapturedFrom().getId().isPresent()) {
            model.setCapturedFrom(this.departmentService.findOne(dto.getCapturedFrom().getId().get()));
        }

        if (ObjectUtils.isNotEmpty(dto.getLabBillTestRequest())) {
            model.setLabTestRequest(new LabTestRequest(dto.getLabBillTestRequest().getId()));
        }

        return model;
    }

    public LabSpecimenCollectionDto mapLabSpecimenModelToDto(LabSpecimenCollection model) {
        LabSpecimenCollectionDto dto = new LabSpecimenCollectionDto();
        this.setLabSpecimenCollectionDtoPre(dto, model);

        LabTestRequest labTestRequest = model.getLabTestRequest();
        if (ObjectUtils.isNotEmpty(labTestRequest.getPatient())) {
            dto.setPatient(labTestRequest.getPatient().transformToDto());
        }
        if (!labTestRequest.getTestItems().isEmpty()) {
            List<LabBillTestItemsDto> testItemsDtoList = labTestRequest.getTestItems().stream().map(
                    e -> new LabBillTestItemsDto(
                            e.getId(),
                            e.getLabTestRequest().getCode(),
                            e.getBillItem().getProductService().getName(),
                            new LabSpecimenDto(e.getSpecimen().getId(), e.getSpecimen().getName()),
                            e.getAcknowledgement(), model.getCapturedBy().getFullName())
            ).collect(Collectors.toList());

            dto.setLabBillTestRequest(new LabBillTestRequestDto(labTestRequest.getId(), labTestRequest.getDate(), testItemsDtoList));
        }
        return dto;
    }

    private void setLabSpecimenCollectionDtoPre(LabSpecimenCollectionDto dto, LabSpecimenCollection model){
        dto.setId(model.getId());
        dto.setOtherInformation(model.getOtherInformation());
        dto.setProvisionalDiagnosis(model.getProvisionalDiagnosis());
        dto.setClinicalSummary(model.getClinicalSummary());
    }
}
