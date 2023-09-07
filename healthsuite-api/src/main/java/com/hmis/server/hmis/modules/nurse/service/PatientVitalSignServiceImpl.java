package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.socket.SockAsyncService;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.PatientVitalSignSearchDto;
import com.hmis.server.hmis.modules.nurse.dto.VitalSignDto;
import com.hmis.server.hmis.modules.nurse.iservice.IPatientVitalSignService;
import com.hmis.server.hmis.modules.nurse.model.PatientVitalSign;
import com.hmis.server.hmis.modules.nurse.repository.PatientVitalSignServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.*;

import static com.hmis.server.hmis.common.constant.HmisConstant.SAVED_SUCCESSFULLY;

@Service
@Slf4j
public class PatientVitalSignServiceImpl implements IPatientVitalSignService {
    private final PatientVitalSignServiceRepository vitalSignServiceRepository;
    private final DepartmentServiceImpl departmentService;
    private final UserServiceImpl userService;
    private final PatientDetailServiceImpl patientDetailService;
    private final SockAsyncService sockAsyncService;
    private final PatientClerkActivityService activityService;
    private final HmisUtilService utilService;

    public PatientVitalSignServiceImpl(
            PatientVitalSignServiceRepository vitalSignServiceRepository,
            DepartmentServiceImpl departmentService,
            UserServiceImpl userService,
            PatientDetailServiceImpl patientDetailService,
            @Lazy SockAsyncService sockAsyncService,
            PatientClerkActivityService activityService,
            @Lazy HmisUtilService utilService) {
        this.vitalSignServiceRepository = vitalSignServiceRepository;
        this.departmentService = departmentService;
        this.userService = userService;
        this.patientDetailService = patientDetailService;
        this.sockAsyncService = sockAsyncService;
        this.activityService = activityService;
        this.utilService = utilService;
    }


    @Override
    public ResponseDto createPatientVitalSign(VitalSignDto dto) {
        this.validateVitalSignDtoBeforeSave(dto);
        try {
            PatientVitalSign vitalSign = this.mapDtoToModel(dto);
            PatientVitalSign sign = this.vitalSignServiceRepository.save(vitalSign);
            this.activityService.savePatientVitalSignActivity(vitalSign);
            this.asyncAddToDoctorWaitingList(sign);
            return new ResponseDto(SAVED_SUCCESSFULLY);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public VitalSignDto findPatientLastCapturedVitalSign(Long patientId) {
        PatientVitalSign model = this.vitalSignServiceRepository.findTopByPatientOrderByIdDesc(new PatientDetail(patientId));
        return this.mapVitalSignModelToDto(model);
    }

    public VitalSignDto findPatientVitalSign(Long patientId) {
        return null;
    }

    public VitalSignDto mapVitalSignModelToDto(PatientVitalSign model) {
        VitalSignDto dto = new VitalSignDto();
        if (ObjectUtils.isNotEmpty(model.getPatient())) {
            dto.setPatient(this.patientDetailService.mapToPatientDto(model.getPatient()));
        }
        if (ObjectUtils.isNotEmpty(model.getCaptureFromLocation())) {
            dto.setCaptureFromLocation(this.departmentService.mapModelToDto(model.getCaptureFromLocation()));
        }
        if (ObjectUtils.isNotEmpty(model.getCapturedBy())) {
            User doc = model.getCapturedBy();
            dto.setCapturedBy(this.userService.mapToDtoClean(doc));
            dto.setCapturedByLabel(model.getCapturedBy().getFullName());
        }
        if (ObjectUtils.isNotEmpty(model.getWeight())) {
            dto.setWeight(model.getWeight());
        }
        if (ObjectUtils.isNotEmpty(model.getHeight())) {
            dto.setHeight(model.getHeight());
        }
        if (ObjectUtils.isNotEmpty(model.getBodyMassIndex())) {
            dto.setBodyMassIndex(model.getBodyMassIndex());
        }
        if (ObjectUtils.isNotEmpty(model.getTemperature())) {
            dto.setTemperature(model.getTemperature());
        }
        if (model.getBodySurfaceArea() != null) {
            dto.setBodySurfaceArea(model.getBodySurfaceArea());
        }
        if (model.getRespiratoryRate() != null) {
            dto.setRespiratoryRate(model.getRespiratoryRate());
        }
        if (model.getPulseRate() != null) {
            dto.setPulseRate(model.getPulseRate());
        }
        if (model.getSystolicBp() != null) {
            dto.setSystolicBp(model.getSystolicBp());
        }
        if (model.getDiastolicBp() != null) {
            dto.setDiastolicBp(model.getDiastolicBp());
        }
        if (model.getRandomBloodSugar() != null) {
            dto.setRandomBloodSugar(model.getRandomBloodSugar());
        }
        if (model.getFastBloodSugar() != null) {
            dto.setFastBloodSugar(model.getFastBloodSugar());
        }
        if (model.getOxygenSaturation() != null) {
            dto.setOxygenSaturation(model.getOxygenSaturation());
        }
        if (model.getPainScore() != null) {
            dto.setPainScore(model.getPainScore());
        }
        if (model.getUrineAnalysis() != null) {
            dto.setUrineAnalysis(model.getUrineAnalysis());
        }
        if (model.getCommentRemark() != null) {
            dto.setCommentRemark(model.getCommentRemark());
        }
        if (model.getAssignTo() != null) {
            dto.setAssignTo(this.userService.mapToDtoClean(model.getAssignTo()));
        }
        if (model.getIsDoctorModule() != null) {
            dto.setIsDoctor(model.getIsDoctorModule());
        }
        if (model.getIsNurseModule() != null) {
            dto.setIsNurse(model.getIsNurseModule());
        }
        return dto;
    }

    public Map<String, List<?>> findPatientVitalSignByDateRange(PatientVitalSignSearchDto dto) {
        try {
            LocalDate startDate = this.utilService.transformToLocalDate(dto.getStartDate());
            LocalDate endDate = this.utilService.transformToLocalDate(dto.getEndDate());
            PatientDetail patient = this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId());
            List<PatientVitalSign> list = this.vitalSignServiceRepository.findTop5ByDateIsLessThanEqualAndDateGreaterThanEqualAndPatientOrderByDateDesc(endDate, startDate, patient);

            Map<String, List<?>> map = new HashMap<>();
            if (!list.isEmpty()) {
                List<String> dateValues = new ArrayList<>();
                List<String> timeValues = new ArrayList<>();
                List<Double> fastBloodSugarValues = new ArrayList<>();
                List<Double> randomBloodSugarValues = new ArrayList<>();
                List<Double> painScoreValues = new ArrayList<>();
                List<Double> oxygenSaturationValues = new ArrayList<>();
                List<Double> urinalysisValues = new ArrayList<>();
                List<Double> pulseValues = new ArrayList<>();
                List<Double> respRateValues = new ArrayList<>();
                List<Double> bpDiastolicValues = new ArrayList<>();
                List<Double> bpSystolicValues = new ArrayList<>();
                List<Double> tempValues = new ArrayList<>();

                for (PatientVitalSign sign : list) {
                    dateValues.add(sign.getDate().toString());
                    timeValues.add(sign.getTime().toString());

                    fastBloodSugarValues.add(sign.getFastBloodSugar());
                    randomBloodSugarValues.add(sign.getRandomBloodSugar());
                    painScoreValues.add(sign.getPainScore());
                    oxygenSaturationValues.add(sign.getOxygenSaturation());
                    urinalysisValues.add(sign.getUrineAnalysis());

                    pulseValues.add(sign.getPulseRate());
                    respRateValues.add(sign.getRespiratoryRate());
                    bpDiastolicValues.add(sign.getDiastolicBp());
                    bpSystolicValues.add(sign.getSystolicBp());
                    tempValues.add(sign.getTemperature());

                }
                map.put("dateValues", dateValues);
                map.put("timeValues", timeValues);
                map.put("fastBloodSugarValues", fastBloodSugarValues);
                map.put("randomBloodSugarValues", randomBloodSugarValues);
                map.put("painScoreValues", painScoreValues);
                map.put("oxygenSaturationValues", oxygenSaturationValues);
                map.put("urinalysisValues", urinalysisValues);
                map.put("pulseValues", pulseValues);
                map.put("respRateValues", respRateValues);
                map.put("bpDiastolicValues", bpDiastolicValues);
                map.put("bpSystolicValues", bpSystolicValues);
                map.put("tempValues", tempValues);
            }

            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void asyncAddToDoctorWaitingList(PatientVitalSign sign) {
        // async
        Long patientId = sign.getPatient().getId();
        Long clinicId = sign.getCaptureFromLocation().getId();
        Optional<Long> doctorId = Optional.empty();
        if (ObjectUtils.isNotEmpty(sign.getAssignTo())) {
            doctorId = Optional.ofNullable(sign.getAssignTo().getId());
        }
        this.sockAsyncService.addToDoctorWaitingList(patientId, clinicId, doctorId);
    }

    private PatientVitalSign mapDtoToModel(VitalSignDto dto) {
        PatientVitalSign patientVitalSign = new PatientVitalSign();

        if (ObjectUtils.isNotEmpty(dto.getPatient())) {
            patientVitalSign.setPatient(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
        }

        if (ObjectUtils.isNotEmpty(dto.getCaptureFromLocation())) {
            patientVitalSign.setCaptureFromLocation(this.departmentService.findOneRaw(dto.getCaptureFromLocation().getId()));
        }

        if (ObjectUtils.isNotEmpty(dto.getCapturedBy())) {
            patientVitalSign.setCapturedBy(this.userService.findOneRaw(dto.getCapturedBy().getId().get()));
        }

        if (dto.getWeight() != null) {
            patientVitalSign.setWeight(dto.getWeight());
        }

        if (dto.getHeight() != null) {
            patientVitalSign.setHeight(dto.getHeight());
        }

        if (dto.getBodyMassIndex() != null) {
            patientVitalSign.setBodyMassIndex(dto.getBodyMassIndex());
        }

        if (dto.getTemperature() != null) {
            patientVitalSign.setTemperature(dto.getTemperature());
        }

        if (dto.getBodySurfaceArea() != null) {
            patientVitalSign.setBodySurfaceArea(dto.getBodySurfaceArea());
        }

        if (dto.getRespiratoryRate() != null) {
            patientVitalSign.setRespiratoryRate(dto.getRespiratoryRate());
        }

        if (dto.getPulseRate() != null) {
            patientVitalSign.setPulseRate(dto.getPulseRate());
        }

        if (dto.getSystolicBp() != null) {
            patientVitalSign.setSystolicBp(dto.getSystolicBp());
        }

        if (dto.getDiastolicBp() != null) {
            patientVitalSign.setDiastolicBp(dto.getDiastolicBp());
        }

        if (dto.getRandomBloodSugar() != null) {
            patientVitalSign.setRandomBloodSugar(dto.getRandomBloodSugar());
        }

        if (dto.getFastBloodSugar() != null) {
            patientVitalSign.setFastBloodSugar(dto.getFastBloodSugar());
        }

        if (dto.getOxygenSaturation() != null) {
            patientVitalSign.setOxygenSaturation(dto.getOxygenSaturation());
        }

        if (dto.getPainScore() != null) {
            patientVitalSign.setPainScore(dto.getPainScore());
        }

        if (dto.getUrineAnalysis() != null) {
            patientVitalSign.setUrineAnalysis(dto.getUrineAnalysis());
        }

        if (dto.getCommentRemark() != null) {
            patientVitalSign.setCommentRemark(dto.getCommentRemark());
        }

        if (dto.getAssignTo() != null) {
            patientVitalSign.setAssignTo(this.userService.findOneRaw(dto.getAssignTo().getId().get()));
        }

        if (dto.getIsDoctor() != null) {
            patientVitalSign.setIsDoctorModule(dto.getIsDoctor());
        }

        if (dto.getIsNurse() != null) {
            patientVitalSign.setIsNurseModule(dto.getIsNurse());
        }

        return patientVitalSign;
    }

    private void validateVitalSignDtoBeforeSave(VitalSignDto dto) {
        if (ObjectUtils.isEmpty(dto.getPatient())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is required");
        }
        /*
        if (ObjectUtils.isEmpty(dto.getWeight())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient weight is required");
        }
        if (ObjectUtils.isEmpty(dto.getHeight())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient height is required");
        }
        if (ObjectUtils.isEmpty(dto.getTemperature())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient temperature is required");
        }
        if (ObjectUtils.isEmpty(dto.getCapturedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attending Officer is required");
        }
        if (ObjectUtils.isEmpty(dto.getCaptureFromLocation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attending Location is required");
        }
         */
    }
}
