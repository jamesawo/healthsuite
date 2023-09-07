package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.LabSpecimen;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.dto.BillPatientTypeEnum;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.billing.service.BillServiceImpl;
import com.hmis.server.hmis.modules.clearking.service.DoctorRequestServiceImpl;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.*;
import com.hmis.server.hmis.modules.lab.model.*;
import com.hmis.server.hmis.modules.lab.repository.LabBillTestRequestRepository;
import com.hmis.server.hmis.modules.lab.repository.LabSpecimenAckRepository;
import com.hmis.server.hmis.modules.lab.repository.LabTestRequestItemsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.LAB_TEST_REQUEST_PREFIX_DEFAULT;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.LAB_TEST_REQUEST_CODE_PREFIX;
import static com.hmis.server.hmis.modules.lab.dto.LabSampleSearchedByEnum.*;

@Service
@Slf4j
public class LabTestRequestService {
    private final LabBillTestRequestRepository labBillTestRequestRepository;
    private final LabTestRequestItemsRepository labItemsRepository;
    private final PatientDetailServiceImpl patientDetailService;
    private final BillServiceImpl billService;
    private final CommonService commonService;
    private final DoctorRequestServiceImpl doctorRequestService;
    private final LabSpecimenAckRepository labSpecimenAckRepository;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;
    private final LabTestTrackerService testTrackerService;
    private final LabSpecimenCollectionService specimenCollectionService;

    public LabTestRequestService(
            LabBillTestRequestRepository labBillTestRequestRepository,
            LabTestRequestItemsRepository labItemsRepository,
            PatientDetailServiceImpl patientDetailService,
            @Lazy BillServiceImpl billService,
            @Lazy CommonService commonService,
            DoctorRequestServiceImpl doctorRequestService,
            LabSpecimenAckRepository labSpecimenAckRepository,
            @Lazy UserServiceImpl userService,
            @Lazy DepartmentServiceImpl departmentService,
            LabTestTrackerService testTrackerService,
            LabSpecimenCollectionService specimenCollectionService
    ) {
        this.labBillTestRequestRepository = labBillTestRequestRepository;
        this.labItemsRepository = labItemsRepository;
        this.patientDetailService = patientDetailService;
        this.billService = billService;
        this.commonService = commonService;
        this.doctorRequestService = doctorRequestService;
        this.labSpecimenAckRepository = labSpecimenAckRepository;
        this.userService = userService;
        this.departmentService = departmentService;
        this.testTrackerService = testTrackerService;
        this.specimenCollectionService = specimenCollectionService;
    }

    public LabTestRequest findOneLabBillTest(Long billTestId) {
        Optional<LabTestRequest> optional = this.labBillTestRequestRepository.findById(billTestId);
        return optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, HmisConstant.STATUS_404));
    }

    public LabTestRequestItem findOneLabTestItem(Long testItemId) {
        Optional<LabTestRequestItem> optional = this.labItemsRepository.findById(testItemId);
        return optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Test"));
    }

    public void saveLabTestItems(LabTestItemsWrapper wrapper) {
        try {
            LabTestRequest testRequest = new LabTestRequest();
            PatientBill bill = wrapper.getBill();
            testRequest.setInvoiceNumber(bill.getInvoiceNumber());
            if (bill.getIsPaid()) testRequest.setReceiptNumber(bill.getReceiptNumber());
            if (bill.getIsDoctorRequest() && bill.getDoctorRequest() != null) {
                testRequest.setIsDoctorRequest(bill.getIsDoctorRequest());
                testRequest.setRequestLab(bill.getDoctorRequest().getLabRequest());
            }
            BillPatientTypeEnum billType = BillPatientTypeEnum.valueOf(bill.getBillPatientType());
            if (billType.equals(BillPatientTypeEnum.REGISTERED) && bill.getPatient() != null) {
                testRequest.setPatient(bill.getPatient());
            } else if (billType.equals(BillPatientTypeEnum.WALK_IN) && bill.getWalkInPatient() != null) {
                testRequest.setWalkInPatient(bill.getWalkInPatient());
            }
            testRequest.setBill(bill);
            testRequest.setCode(this.generateCode());
            LabTestRequest savedLabTest = this.labBillTestRequestRepository.save(testRequest);
            this.saveLabTestItems(wrapper.getItemsList(), savedLabTest);
            this.testTrackerService.saveTrackerBill(savedLabTest, savedLabTest.getBill().getIsCredit());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ResponseEntity<Map<String, Object>> searchLabTestRequest(LabSampleSearchedByEnum searchBy, String term) {
        try {
            List<LabTestRequest> testList = this.searchLabTestRequestByType(searchBy, term);
            List<LabBillTestRequestDto> testListDto = new ArrayList<>();
            LabSpecimenCollectionDto sampleCollection = new LabSpecimenCollectionDto();
            if (!testList.isEmpty()) {
                testListDto = testList.stream().map(this::mapLabTestRequestToDto).collect(Collectors.toList());
                LabTestRequest labTestRequest = testList.get(0);
                sampleCollection = this.specimenCollectionService.getLabTestSearchSampleCollectionClean(labTestRequest.getId());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("testItems", testListDto);
            map.put("sampleCollectionData", sampleCollection);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /* Update test record, add receipt number when a bill is paid and the bill is for lab test  */
    public void updateLabTestReceiptNumber(String billNumber, String receiptNumber) {
        try {
            Optional<LabTestRequest> optional = this.labBillTestRequestRepository.findByInvoiceNumber(billNumber);
            if (optional.isPresent()) {
                LabTestRequest testRequest = optional.get();
                testRequest.setReceiptNumber(receiptNumber);
                this.labBillTestRequestRepository.save(testRequest);
                this.testTrackerService.saveTrackerBill(testRequest, testRequest.getBill().getIsCredit());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<MessageDto> saveLabSampleCollection(LabSpecimenCollectionDto dto) {
        if (this.isSpecimenCollected(dto.getLabBillTestRequest().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specimen Is Already Collected, Kindly Update");
        }

        try {
            User user = this.userService.findOneRaw(dto.getCapturedBy().getId().get());
            Department department = this.departmentService.findOne(dto.getCapturedFrom().getId().get());
            LabSpecimenCollection sample = this.specimenCollectionService.mapLabSpecimenCollectionToModel(dto);
            Boolean isAcknowledgeSpecimen = dto.getIsAcknowledgeSpecimen();
            List<LabBillTestItemsDto> testItems = dto.getLabBillTestRequest().getTestItems();
            LabSpecimenCollection specimenCollection = this.specimenCollectionService.saveSpecimenCollection(sample);
            LabTestRequest labTestRequest = specimenCollection.getLabTestRequest();

            // update lab test item specimen collection status
            this.updateLabTestSpecimenList(specimenCollection, testItems, isAcknowledgeSpecimen);

            // if enable specimen ack during sample collection is enabled on global setting

            if (isAcknowledgeSpecimen) {
                this.updateLabTestAcknowledgement(labTestRequest, testItems, user, dto.getLabBillTestRequest().getCode(), department);
            }
            // save sample collection in lab test tracker
            this.testTrackerService.saveSampleCollection(labTestRequest, specimenCollection);

            this.setLabTestSpecimenAlreadyCollected(dto.getLabBillTestRequest().getId(), true);
            return ResponseEntity.ok(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<MessageDto> saveLabSampleAcknowledgement(LabSpecimenCollectionDto dto) {
        try {
            User user = this.userService.findOneRaw(dto.getCapturedBy().getId().get());
            Department department = this.departmentService.findOne(dto.getCapturedFrom().getId().get());
            List<LabBillTestItemsDto> testItems = dto.getLabBillTestRequest().getTestItems();
            LabTestRequest labTestRequest = this.findOneLabBillTest(dto.getLabBillTestRequest().getId());
            this.updateLabTestAcknowledgement(labTestRequest, testItems, user, dto.getLabBillTestRequest().getCode(), department);
            return ResponseEntity.ok().body(new MessageDto(HmisConstant.UPDATED_MESSAGE));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void updateLabTestAcknowledgement(
            LabTestRequest labTestRequest,
            List<LabBillTestItemsDto> items,
            User user,
            String labNumber,
            Department department
    ) {
        if (!items.isEmpty()) {
            for (LabBillTestItemsDto item : items) {
                if (item.getId() != null) {
                    Optional<LabTestRequestItem> optional = this.labItemsRepository.findById(item.getId());
                    if (optional.isPresent()) {
                        LabTestRequestItem labTest = optional.get();
                        LabSpecimenAck ack = new LabSpecimenAck();
                        Optional<LabSpecimenAck> optionalLabSpecimenAck = this.findLabSpecimenAckByLabTestItem(labTest);
                        optionalLabSpecimenAck.ifPresent(specimenAck -> ack.setId(specimenAck.getId()));
                        ack.setLabTestRequest(labTestRequest);
                        ack.setLabTestRequestItem(labTest);
                        ack.setLabNumber(labNumber);
                        ack.setCapturedBy(user);
                        ack.setCapturedFrom(department);
                        this.labSpecimenAckRepository.save(ack);
                        this.testTrackerService.saveSampleAck(labTestRequest, labTest, ack);
                    }
                }
            }
        }
    }

    public ResponseEntity<LabTestTrackerDto> trackLabTest(Long testId, Long labBillTestRequestId) {
        try {
            LabTestRequest billTest = this.findOneLabBillTest(labBillTestRequestId);
            LabTestTrackerDto tracker = this.testTrackerService.findTracker(billTest, testId);
            return ResponseEntity.ok().body(tracker);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    public Optional<LabSpecimenAck> findLabSpecimenAckByLabTestItem(LabTestRequestItem item) {
        return this.labSpecimenAckRepository.findByLabTestRequestItem(item);
    }

    /* get lab test collected samples for acknowledgment */
    public ResponseEntity<LabSpecimenCollectionDto> getLabTestSampleCollection(Long labTestId) {
        try {
            LabTestRequest billTestRequest = this.findOneLabBillTest(labTestId);
            LabSpecimenCollectionDto dto = this.specimenCollectionService.findByLabTestReqToDto(billTestRequest);
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void updateLabTestSpecimenList(
            LabSpecimenCollection collection,
            List<LabBillTestItemsDto> items,
            boolean isAcknowledgeSpecimen) {
        if (!items.isEmpty()) {
            for (LabBillTestItemsDto item : items) {
                if (item.getId() != null) {
                    Optional<LabTestRequestItem> optional = this.labItemsRepository.findById(item.getId());
                    if (optional.isPresent()) {
                        LabTestRequestItem labTest = optional.get();
                        labTest.setSampleStatus(item.getSpecimenStatus());
                        labTest.setSpecimen(new LabSpecimen(item.getSpecimenDto().getId().get()));
                        if (isAcknowledgeSpecimen) {
                            labTest.setAcknowledgement(item.getAcknowledgement());
                        }
                        this.labItemsRepository.save(labTest);
                        this.specimenCollectionService.updateSpecimenCollectionWithTestItem(collection, labTest);
                    }
                }
            }
        }
    }

    private LabBillTestRequestDto mapLabTestRequestToDto(LabTestRequest model) {
        LabBillTestRequestDto dto = new LabBillTestRequestDto();
        dto.setId(model.getId());
        if (ObjectUtils.isNotEmpty(model.getInvoiceNumber())) {
            dto.setInvoiceNumber(model.getInvoiceNumber());
        }
        if (ObjectUtils.isNotEmpty(model.getReceiptNumber())) {
            dto.setReceiptNumber(model.getReceiptNumber());
        }
        if (ObjectUtils.isNotEmpty(model.getLabNumber())) {
            dto.setLabNumber(model.getLabNumber());
        }
        if (ObjectUtils.isNotEmpty(model.getDate())) {
            dto.setDate(model.getDate());
        }
        if (ObjectUtils.isNotEmpty(model.getTime())) {
            dto.setTime(model.getTime());
        }
        if (model.getBill() != null && model.getBill().getBillPatientType().equals(BillPatientTypeEnum.REGISTERED.label)) {
            dto.setPatient(model.getBill().getPatient().transformToDto());
        } else {
            dto.setPatient(model.getBill().getWalkInPatient().mapToPatientDto());
        }

        dto.setIsDoctorRequest(model.getIsDoctorRequest());
        dto.setBill(this.billService.mapServiceBillToDto(model.getBill()));
        dto.setCode(model.getCode());

        if (ObjectUtils.isNotEmpty(model.getRequestLab())) {
            dto.setRequestLab(this.doctorRequestService.mapLabRequestToDto(model.getRequestLab()));
        }

        if (!model.getTestItems().isEmpty()) {
            dto.setBill(null);
            List<LabBillTestItemsDto> items = model.getTestItems().stream().map(this::mapLabTestItemToDto).collect(Collectors.toList());
            dto.setTestItems(items);
        }
        return dto;
    }

    private void saveLabTestItems(List<PatientServiceBillItem> itemsList, LabTestRequest savedLabTest) {
        if (!itemsList.isEmpty()) {
            for (PatientServiceBillItem item : itemsList) {
                LabTestRequestItem newItem = new LabTestRequestItem();
                newItem.setBillItem(item);
                newItem.setLabTestRequest(savedLabTest);
                this.labItemsRepository.save(newItem);
            }
        }
    }

    /* Search lab test request for sample collection */
    private List<LabTestRequest> searchLabTestRequestByType(LabSampleSearchedByEnum searchBy, String term) {
        if (searchBy.equals(LAB_NUMBER)) {
            return this.labBillTestRequestRepository.findAllByLabNumber(term);
        } else if (searchBy.equals(INVOICE_NUMBER)) {
            return this.labBillTestRequestRepository.findAllByInvoiceNumber(term);
        } else if (searchBy.equals(PATIENT)) {
            PatientDetail patient = this.patientDetailService.findByPatientNumber(term);
            return this.labBillTestRequestRepository.findAllByPatient(patient);
        } else if (searchBy.equals(RECEIPT_NUMBER)) {
            return this.labBillTestRequestRepository.findAllByReceiptNumber(term);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Search Type");
        }
    }

    private String generateCode() {
        GenerateCodeDto generateCodeDto = new GenerateCodeDto();
        generateCodeDto.setDefaultPrefix(LAB_TEST_REQUEST_PREFIX_DEFAULT);
        generateCodeDto.setGlobalSettingKey(Optional.of(LAB_TEST_REQUEST_CODE_PREFIX));
        generateCodeDto.setLastGeneratedCode(this.labBillTestRequestRepository.findTopByOrderByIdDesc().map(LabTestRequest::getCode));
        return commonService.generateDataCode(generateCodeDto);
    }

    private void setLabTestSpecimenAlreadyCollected(Long labTestRequestId, boolean isCollected) {
        LabTestRequest billTest = findOneLabBillTest(labTestRequestId);
        billTest.setIsSpecimenCollected(isCollected);
        this.labBillTestRequestRepository.save(billTest);
    }

    private boolean isSpecimenCollected(Long labTestRequestId) {
        LabTestRequest billTest = findOneLabBillTest(labTestRequestId);
        return billTest.getIsSpecimenCollected();
    }

    private LabBillTestItemsDto mapLabTestItemToDto(LabTestRequestItem model) {
        LabBillTestItemsDto dto = new LabBillTestItemsDto();
        if (model.getId() != null) {
            dto.setId(model.getId());
        }
        if (model.getLabTestRequest() != null && model.getLabTestRequest().getCode() != null) {
            dto.setRequestNumber(model.getLabTestRequest().getCode());
        }
        if (model.getBillItem() != null && model.getBillItem().getProductService() != null) {
            dto.setTestName(model.getBillItem().getProductService().getName());
        }
        if (model.getSpecimen() != null) {
            dto.setSpecimenDto(new LabSpecimenDto(model.getSpecimen().getId(), model.getSpecimen().getName()));
        }
        dto.setSpecimenStatus(model.getSampleStatus());
        if (model.getAcknowledgement() != null) {
            dto.setAcknowledgement(model.getAcknowledgement());
        }

        Optional<LabSpecimenCollection> optional = this.specimenCollectionService.findLabSampleCollectionByLabTestRequest(model.getLabTestRequest().getId());
        optional.ifPresent(collection -> dto.setCollectedBy(collection.getCapturedBy().getFullName()));

        return dto;
//        e.getId(),
//                e.getLabTestRequest().getCode(),
//                e.getBillItem().getProductService().getName(), new LabSpecimenDto(e.getSpecimen().getId(), e.getSpecimen().getName()
    }
}
