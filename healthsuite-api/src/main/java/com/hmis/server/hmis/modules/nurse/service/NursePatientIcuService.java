package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.PatientIcuBounceDto;
import com.hmis.server.hmis.modules.nurse.model.PatientIcuBounce;
import com.hmis.server.hmis.modules.nurse.repository.PatientIcuBounceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class NursePatientIcuService {
    private final PatientIcuBounceRepository icuBounceRepository;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;
    private final PatientDetailServiceImpl patientDetailService;


    public NursePatientIcuService(PatientIcuBounceRepository icuBounceRepository,
                                  UserServiceImpl userService,
                                  DepartmentServiceImpl departmentService, PatientDetailServiceImpl patientDetailService) {
        this.icuBounceRepository = icuBounceRepository;
        this.userService = userService;
        this.departmentService = departmentService;
        this.patientDetailService = patientDetailService;
    }

    public ResponseEntity<MessageDto> savePatientIcuBounceBack(PatientIcuBounceDto dto) {
        PatientIcuBounce icuBounce = new PatientIcuBounce();
        if (ObjectUtils.isNotEmpty(dto.getCapturedBy()) && ObjectUtils.isNotEmpty(dto.getCapturedBy().getId())) {
            icuBounce.setCaptureBy(this.userService.findOneRaw(dto.getCapturedBy().getId().get()));
        }

        if (ObjectUtils.isNotEmpty(dto.getCapturedFrom()) && ObjectUtils.isNotEmpty(dto.getCapturedFrom().getId())) {
            icuBounce.setCapturedFrom(this.departmentService.findOne(dto.getCapturedFrom().getId().get()));
        }
        if (ObjectUtils.isNotEmpty(dto.getReasonForBounceBack())) {
            icuBounce.setReasonForBounceBack(dto.getReasonForBounceBack());
        }
        if (ObjectUtils.isNotEmpty(dto.getPatient()) && ObjectUtils.isNotEmpty(dto.getPatient().getPatientId())){
            icuBounce.setPatientDetail(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
        }
        this.icuBounceRepository.save(icuBounce);
        return ResponseEntity.ok(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
    }
}
