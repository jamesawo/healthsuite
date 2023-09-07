package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.dto.*;
import com.hmis.server.hmis.common.common.model.Bed;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.common.service.*;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.billing.service.BillServiceImpl;
import com.hmis.server.hmis.modules.clearking.dto.WardTransferDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkPatientWardTransfer;
import com.hmis.server.hmis.modules.clearking.repository.ClerkPatientWardTransferRepository;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.IService.IPatientAdmissionService;
import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionDto;
import com.hmis.server.hmis.modules.emr.dto.PatientAdmissionStatusEnum;
import com.hmis.server.hmis.modules.emr.dto.PatientDischargeDto;
import com.hmis.server.hmis.modules.emr.dto.PatientWardTransferDto;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientAdmissionRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hmis.server.hmis.modules.reports.dto.SearchAdmissionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class PatientAdmissionServiceImpl implements IPatientAdmissionService {

    private final PatientAdmissionRepository admissionRepository;
    private final ClerkPatientWardTransferRepository wardTransferRepository;
    private final PatientDetailServiceImpl patientDetailService;
    private final WardServiceImpl wardService;
    private final BedServiceImpl bedService;
    private final UserServiceImpl userService;
    private final CommonService commonService;
    private final HmisUtilService utilService;
    private final BillServiceImpl billService;
    private final DepartmentServiceImpl departmentService;
    private final PatientClerkActivityService patientActivity;

    @Autowired
    public PatientAdmissionServiceImpl(

            PatientAdmissionRepository admissionRepository,
            ClerkPatientWardTransferRepository wardTransferRepository,
            PatientDetailServiceImpl patientDetailService,
            WardServiceImpl wardService,
            BedServiceImpl bedService,
            UserServiceImpl userService,
            CommonService commonService,
            @Lazy HmisUtilService utilService,
            @Lazy BillServiceImpl billService,
            @Lazy DepartmentServiceImpl departmentService,
            PatientClerkActivityService patientActivity) {
        this.wardTransferRepository = wardTransferRepository;
        this.patientDetailService = patientDetailService;
        this.wardService = wardService;
        this.bedService = bedService;
        this.userService = userService;
        this.commonService = commonService;
        this.admissionRepository = admissionRepository;
        this.utilService = utilService;
        this.billService = billService;
        this.departmentService = departmentService;
        this.patientActivity = patientActivity;
    }

    @Override
    public ResponseEntity<MessageDto> admitPatient(PatientAdmissionDto dto) {
        this.validateBeforePatientAdmission(dto);

        try {
            PatientAdmission admission = this.mapToModel(dto);
            admission.setAdmissionStatus(PatientAdmissionStatusEnum.ADMITTED);
            admission.setCode(this.generateCode());
            admission.setAdmittedTime(LocalTime.now());
            admission.setAdmittedDate(LocalDate.now());
            PatientAdmission patientAdmission = this.admissionRepository.save(admission);
            this.bedService.updateBedIsOccupiedStatus(patientAdmission.getBed().getId(), true);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(patientAdmission.getCode()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public PatientAdmission dischargePatient(PatientDischargeDto dto) {
        Optional<PatientAdmission> optional = this.findActiveAdmissionByPatientId(dto.getPatient().getPatientId());
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NO ADMISSION RECORD");
        }
        try {
            PatientAdmission admission = optional.get();
            LocalDate dischargedDate = this.utilService.transformToLocalDate(dto.getDischargedDate());
            admission.setDischargedBy(this.userService.findOneRaw(dto.getDischargedBy().getId().get()));
            admission.setDischargedDate(dischargedDate);
            admission.setDischargedTime(LocalTime.now());
            admission.setAdmissionStatus(PatientAdmissionStatusEnum.DISCHARGED);
            if (dto.getFinalDiagnosis() != null) {
                admission.setFinalDiagnosis(dto.getFinalDiagnosis());
            }
            if (dto.getOtherComment() != null) {
                admission.setOtherComment(dto.getOtherComment());
            }
            if (dto.getDischargeStatus() != null) {
                admission.setDischargeStatus(dto.getDischargeStatus());
            }
            if (dto.getConsultant() != null && dto.getConsultant().getId().isPresent()) {
                User dischargeConsultant = this.userService.findOneRaw(dto.getConsultant().getId().get());
                admission.setDischargedConsultant(dischargeConsultant);
            }
            if (dto.getLocation() != null && dto.getLocation().getId().isPresent()) {
                Department location = this.departmentService.findOne(dto.getLocation().getId().get());
                admission.setDischargeLocation(location);
            }
            this.admissionRepository.save(admission);
            this.bedService.deAllocateBed(admission.getBed());
            return admission;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isPatientOnAdmission(Long patientId) {
        Optional<PatientAdmission> admission = this.findActiveAdmissionByPatientId(patientId);
        return admission.isPresent();
    }

    @Override
    public PatientAdmissionDto findPatientAdmission(Long patientId) {
        PatientAdmission patientAdmission = this.findPatientAdmissionRaw(patientId);
        return this.mapToDto(patientAdmission);
    }

    public ResponseEntity<MessageDto> transferPatientWard(PatientWardTransferDto dto) {
        this.validateBeforePatientWardTransfer(dto);
        try {
            PatientDetail patient = this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId());
            PatientAdmission patientAdmissionRaw = this.findPatientAdmissionRaw(dto.getPatient().getPatientId());
            if (!patient.getId().equals(patientAdmissionRaw.getPatient().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected Patient & Admission Record Don't Match");
            }

            Bed bed = this.bedService.findOneRaw(dto.getNewBed().getId());
            Ward ward = this.wardService.findOneRaw(dto.getNewWard().getId());
            this.updatePatientWard(patientAdmissionRaw, ward, bed);
            User user = this.userService.findOneRaw(dto.getUser().getId().get());
            Department location = this.departmentService.findOne(dto.getLocation().getId().get());
            boolean hasConsultant = dto.getConsultant() != null;
            User consultant = new User();
            if (hasConsultant){
                consultant = this.userService.findOneRaw(dto.getConsultant().getId().get());
            }
            // update clerk patient ward transfer
            this.updateClerkPatientWardTransfer(new WardTransferDto(
                    patientAdmissionRaw, patient, bed, ward, user, dto.getTransferNote(), location, hasConsultant, consultant
            ));
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("WARD TRANSFERRED SUCCESSFULLY"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /* update patient admission after ward transfer */
    public void updatePatientWard(PatientAdmission admission, Ward ward, Bed bed) {
        PatientAdmission prevAdmission = this.admissionRepository.getOne(admission.getId());
        prevAdmission.setWard(ward);
        prevAdmission.setBed(bed);
        this.admissionRepository.save(prevAdmission);
    }

    public void updateClerkPatientWardTransfer(WardTransferDto transferDto) {
        ClerkPatientWardTransfer transfer = new ClerkPatientWardTransfer();
        transfer.setPatient(transferDto.getPatient());
        transfer.setPreWard(transferDto.getAdmissionBeforeUpdate().getWard());
        transfer.setPrevBedCode(transferDto.getAdmissionBeforeUpdate().getBed().getCode());
        transfer.setNewWard(transferDto.getNewWard());
        transfer.setNewBedCode(transferDto.getNewBed().getCode());
        transfer.setTransferredBy(transferDto.getUser());
        transfer.setTransferNote(transferDto.getNote());
        if (transferDto.getAdmissionBeforeUpdate().getConsultant() != null) {
            transfer.setPrevConsultant(transferDto.getAdmissionBeforeUpdate().getConsultant());
        }
        transfer.setLocation(transferDto.getLocation());
        if (transferDto.isHasConsultant()) {
            transfer.setNewConsultant(transferDto.getConsultant());
        }
        ClerkPatientWardTransfer wardTransfer = this.wardTransferRepository.save(transfer);
        // update patient activity
        this.patientActivity.saveWardTransferActivity(wardTransfer);
    }

    /*
        check if patient is on admission before calling findPatientAdmissionRaw
     */
    @Override
    public PatientAdmission findPatientAdmissionRaw(Long patientId) {
        Optional<PatientAdmission> admission = this.findActiveAdmissionByPatientId(patientId);
        return admission.orElseGet(PatientAdmission::new);
    }

    public String calculatePatientDaysOnAdmission(PatientAdmission admission) {
        LocalDate startDate = admission.getAdmittedDate();
        LocalDate endDate = this.isDischarge(admission) ? admission.getDischargedDate() : LocalDate.now();
        long result = ChronoUnit.DAYS.between(endDate, startDate);
        return String.valueOf(result);
    }

    public boolean isDischarge(PatientAdmission admission) {
        return admission.getAdmissionStatus().equals(PatientAdmissionStatusEnum.DISCHARGED);
    }

    public PatientAdmission findPatientCurrentAdmission(Long patientId) {
        Optional<PatientAdmission> admission = this.findActiveAdmissionByPatientId(patientId);
        if (!admission.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HmisExceptionMessage.NOTHING_FOUND);
        }
        return admission.get();
    }

    public Optional<PatientAdmission> findActiveAdmissionByPatientId(Long patientId) {
        return this.admissionRepository.findByPatientAndAdmissionStatusEquals(new PatientDetail(patientId), PatientAdmissionStatusEnum.ADMITTED);
    }

    @Override
    public List<PatientDetail> findAllAdmittedPatient() {
        return this.admissionRepository.findAll().stream().map(PatientAdmission::getPatient).collect(Collectors.toList());
    }

    public PatientAdmission findByCode(String code) {
        Optional<PatientAdmission> admission = this.admissionRepository.findByCode(code);
        if (!admission.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HmisExceptionMessage.NOTHING_FOUND);
        }
        return admission.get();
    }

    public List<PatientAdmission> findPatientAdmissionByDateRange(LocalDate startDate, LocalDate endDate, PatientDetail patientDetail) {
        return this.admissionRepository.findAllByPatientAndAdmittedDateIsLessThanEqualAndAdmittedDateIsGreaterThanEqual(
                patientDetail,
                endDate,
                startDate
        );
    }

    public List<SearchAdmissionDto> searchAdmissionSessionByDateRange(LocalDate startDate, LocalDate endDate, PatientDetail patientDetail) {
        List<SearchAdmissionDto> dtoList = new ArrayList<>();
        List<PatientAdmission> admissions = findPatientAdmissionByDateRange(startDate, endDate, patientDetail);
        if (!admissions.isEmpty()) {
            dtoList = admissions.stream().map(this::mapAdmissionToSearchDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    public SearchAdmissionDto findPatientCurrentAdmissionSession(Long patientId) {
        Optional<PatientAdmission> optional = this.findActiveAdmissionByPatientId(patientId);
        return optional.map(this::mapAdmissionToSearchDto).orElse(new SearchAdmissionDto());
    }

    public SearchAdmissionDto mapDischargedAdmissionToSearchDto(PatientAdmission admission) {
        return this.mapAdmissionToSearchDto(admission);
    }

    private SearchAdmissionDto mapAdmissionToSearchDto(PatientAdmission admission) {
        SearchAdmissionDto dto = new SearchAdmissionDto();
        if (admission.getAdmittedDate() != null) {
            dto.setAdmittedDate(admission.getAdmittedDate().toString());
        }
        if (admission.getDischargedDate() != null) {
            dto.setDischargedDate(admission.getDischargedDate().toString());
        }
        if (admission.getAdmittedTime() != null) {
            dto.setAdmittedTime(admission.getAdmittedTime().format(this.utilService.getTimeFormatter()));
        }
        if (admission.getDischargedTime() != null) {
            dto.setDischargedTime(admission.getDischargedTime().format(this.utilService.getTimeFormatter()));
        }
        if (admission.getPatient() != null) {
            dto.setPatient(admission.getPatient().transformToDto());
        }
        if (admission.getWard() != null) {
            DepartmentDto departmentDto = new DepartmentDto(admission.getWard().getDepartment().getId(), admission.getWard().getDepartment().getName());
            WardDto wardDto = new WardDto();
            wardDto.setDepartment(departmentDto);
            wardDto.setCode(admission.getWard().getCode());
            dto.setWard(wardDto);
        }
        if (admission.getBed() != null) {
            dto.setBed(admission.getBed().getCode());
        }
        if (admission.getCode() != null) {
            dto.setAdmissionNumber(admission.getCode());
        }
        dto.setNetAmount(this.billService.getPatientBillTotalByAdmissionSession(admission));
        dto.setIsOnAdmission(admission.getAdmissionStatus().equals(PatientAdmissionStatusEnum.ADMITTED));
        return dto;
    }

    private PatientAdmission mapToModel(PatientAdmissionDto dto) {
        PatientAdmission model = new PatientAdmission();
        if (dto.getId() != null) model.setId(dto.getId());
        if (dto.getPatientId() != null)
            model.setPatient(this.patientDetailService.findPatientDetailById(dto.getPatientId()));
        if (dto.getWardId() != null) model.setWard(this.wardService.findOneRaw(dto.getWardId()));
        if (dto.getAdmissionCode() != null) model.setCode(dto.getAdmissionCode());
        if (dto.getBedId() != null) model.setBed(this.bedService.findOneRaw(dto.getBedId()));
        if (dto.getConsultantId() != null) model.setConsultant(this.userService.findOneRaw(dto.getConsultantId()));
        if (dto.getAdmissionStatus() != null) model.setAdmissionStatus(dto.getAdmissionStatus());
        if (dto.getAdmittedBy() != null && dto.getAdmittedBy().getId().isPresent()) {
            model.setAdmittedBy(new User(dto.getAdmittedBy().getId().get()));
        }
        if (dto.getLocation() != null && dto.getLocation().getId().isPresent()) {
            model.setAdmittedLocation(new Department(dto.getLocation().getId().get()));
        }
        return model;
    }

    private PatientAdmissionDto mapToDto(PatientAdmission admission) {
        PatientAdmissionDto patientAdmissionDto = new PatientAdmissionDto();
        if (admission.getId() != null) {
            patientAdmissionDto.setId(admission.getId());
        }

        if (admission.getCode() != null) {
            patientAdmissionDto.setAdmissionCode(admission.getCode());
        }

        if (admission.getBed() != null) {
            patientAdmissionDto.setBed(new BedDto(admission.getBed().getCode()));
        }

        if (admission.getWard() != null) {
            WardDto wardDto = new WardDto(admission.getWard().getId(), admission.getWard().getCode());
            wardDto.setTitle(admission.getWard().getDepartment().getName());
            patientAdmissionDto.setWard(wardDto);
        }

        if (admission.getAdmittedDate() != null) {
            patientAdmissionDto.setAdmissionDate(this.utilService.transformToDateDto(admission.getAdmittedDate()));
        }
        if (admission.getAdmittedTime() != null) {
            patientAdmissionDto.setAdmittedTime(admission.getAdmittedTime().toString());
        }

        return patientAdmissionDto;
    }

    private String generateCode() {
        GenerateCodeDto codeDto = new GenerateCodeDto();

        codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.ADMISSION_CODE_PREFIX));
        codeDto.setDefaultPrefix(HmisCodeDefaults.ADMISSION_CODE_DEFAULT);
        codeDto.setLastGeneratedCode(this.admissionRepository
                .findTopByOrderByIdDesc()
                .map(PatientAdmission::getCode));
        return commonService.generateDataCode(codeDto);
    }

    private void validateBeforePatientAdmission(PatientAdmissionDto dto) {
        if (this.isPatientOnAdmission(dto.getPatientId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is already on admission ");
        }
        if (dto.getPatientId() == null || dto.getWardId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide patient / ward ");
        }
    }

    private void validateBeforePatientWardTransfer(PatientWardTransferDto dto) {
        if (dto.getPatient() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is required ");
        }
        if (dto.getTransferNote() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer Note is required");
        }
        if (dto.getNewWard() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Ward is required");
        }

    }


}
