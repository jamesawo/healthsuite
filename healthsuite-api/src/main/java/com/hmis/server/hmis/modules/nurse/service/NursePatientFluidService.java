package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.PatientFluidBalanceDto;
import com.hmis.server.hmis.modules.nurse.dto.SubReportDto;
import com.hmis.server.hmis.modules.nurse.model.PatientFluidBalance;
import com.hmis.server.hmis.modules.nurse.repository.PatientFluidBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum.CLERK_VITAL_SIGN;
import static com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum.NURSE_PATIENT_FLUID_BALANCE;

@Service @Slf4j
public class NursePatientFluidService {
    private final PatientFluidBalanceRepository patientFluidBalanceRepository;
    private final PatientDetailServiceImpl patientDetailService;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;
    private final HmisUtilService utilService;
    private final CommonService commonService;
    private final NursePatientCardNoteServiceImpl cardNoteService;

    public NursePatientFluidService(
            PatientFluidBalanceRepository patientFluidBalanceRepository,
            PatientDetailServiceImpl patientDetailService,
            UserServiceImpl userService, DepartmentServiceImpl departmentService,
            HmisUtilService utilService, CommonService commonService,
            @Lazy NursePatientCardNoteServiceImpl cardNoteService) {
        this.patientFluidBalanceRepository = patientFluidBalanceRepository;
        this.patientDetailService = patientDetailService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.utilService = utilService;
        this.commonService = commonService;
        this.cardNoteService = cardNoteService;
    }

    public ResponseEntity<MessageDto> savePatientFluidBalance(PatientFluidBalanceDto dto){
        try {
            PatientFluidBalance fluidBalance = this.mapDtoToModel(dto);
            this.patientFluidBalanceRepository.save(fluidBalance);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<byte[]> getPatientFluidBalance(PatientDetailDto dto){
        PatientDetail patient = this.patientDetailService.findPatientDetailById(dto.getPatientId());
        Map<LocalDate, List<PatientFluidBalance>> collection = this.patientFluidBalanceRepository.findAllByPatientDetail(patient).parallelStream().collect(Collectors.groupingBy(PatientFluidBalance::getDate));
        Map<String, Object> objectMap = this.cardNoteService.prepPatientEFolderRecord(null,"", patient, "PATIENT FLUID BALANCE ENTRIES");
        List<SubReportDto> fieldList = this.wrapToSubReportDto(collection);

        byte[] bytes = this.cardNoteService.exportPatientCardNoteReport(objectMap, fieldList);
        return ResponseEntity.status(HttpStatus.OK).body(bytes);
    }


    private  List<SubReportDto> wrapToSubReportDto(Map<LocalDate, List<PatientFluidBalance>> collection){
        List<SubReportDto> fieldList = new ArrayList<>();
        if (collection != null && !collection.isEmpty()){
            for (Map.Entry<LocalDate, List<PatientFluidBalance>> entry : collection.entrySet()) {
                List<PatientFluidBalanceDto> balanceDtos  = new ArrayList<>();
                for (PatientFluidBalance patientFluidBalance : entry.getValue()) {
                    balanceDtos.add(this.mapModelToDto(patientFluidBalance));
                }
                JasperReport report = this.commonService.compileReportDesign(this.utilService, NURSE_PATIENT_FLUID_BALANCE);
                fieldList.add(new SubReportDto(
                        new JRBeanCollectionDataSource(balanceDtos),report, entry.getKey().toString() ));
            }
        }
        return fieldList;
    }


    private PatientFluidBalance mapDtoToModel(PatientFluidBalanceDto dto) {
        PatientFluidBalance model = new PatientFluidBalance();
        if (ObjectUtils.isNotEmpty(dto.getId())) model.setId(dto.getId());
        if (ObjectUtils.isNotEmpty(dto.getInputType())) model.setInputType(dto.getInputType());
        if (ObjectUtils.isNotEmpty(dto.getBlood())) model.setBlood(dto.getBlood());
        if (ObjectUtils.isNotEmpty(dto.getTube())) model.setTube(dto.getTube());
        if (ObjectUtils.isNotEmpty(dto.getOral())) model.setOral(dto.getOral());
        if (ObjectUtils.isNotEmpty(dto.getIv())) model.setIv(dto.getIv());
        if (ObjectUtils.isNotEmpty(dto.getTotalIntake())) model.setTotalIntake(dto.getTotalIntake());
        if (ObjectUtils.isNotEmpty(dto.getBalance())) model.setBalance(dto.getBalance());
        if (ObjectUtils.isNotEmpty(dto.getTotalOutput())) model.setTotalOutput(dto.getTotalOutput());
        if (ObjectUtils.isNotEmpty(dto.getUrine())) model.setUrine(dto.getUrine());
        if (ObjectUtils.isNotEmpty(dto.getTubeVomit())) model.setTubeVomit(dto.getTubeVomit());
        if (ObjectUtils.isNotEmpty(dto.getDrainFaeces())) model.setDrainFaeces(dto.getDrainFaeces());
        if (ObjectUtils.isNotEmpty(dto.getOutputType())) model.setOutputType(dto.getOutputType());
        if (ObjectUtils.isNotEmpty(dto.getAdditionalInformation())) model.setOtherInformation(dto.getAdditionalInformation());

        if (ObjectUtils.isNotEmpty(dto.getPatient())  && ObjectUtils.isNotEmpty(dto.getPatient().getPatientId())) {
            model.setPatientDetail(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
        }
        if (ObjectUtils.isNotEmpty(dto.getCaptureBy()) && ObjectUtils.isNotEmpty(dto.getCaptureBy().getId())){
            model.setCaptureBy(this.userService.findOneRaw(dto.getCaptureBy().getId().get()));
        }

        if (ObjectUtils.isNotEmpty(dto.getCapturedFrom()) && ObjectUtils.isNotEmpty(dto.getCapturedFrom().getId())){
            model.setCapturedFrom(this.departmentService.findOne(dto.getCapturedFrom().getId().get()));
        }
        return model;
    }

    private PatientFluidBalanceDto mapModelToDto(PatientFluidBalance model) {
        PatientFluidBalanceDto dto1 = new PatientFluidBalanceDto();
        dto1.setCaptureDate(model.getDate().toString());
        if (ObjectUtils.isNotEmpty(model.getId())) dto1.setId(model.getId());
        if (ObjectUtils.isNotEmpty(model.getInputType())) dto1.setInputType(model.getInputType());
        if (ObjectUtils.isNotEmpty(model.getBlood())) dto1.setBlood(model.getBlood());
        if (ObjectUtils.isNotEmpty(model.getTube())) dto1.setTube(model.getTube());
        if (ObjectUtils.isNotEmpty(model.getOral())) dto1.setOral(model.getOral());
        if (ObjectUtils.isNotEmpty(model.getIv())) dto1.setIv(model.getIv());
        if (ObjectUtils.isNotEmpty(model.getTotalIntake())) dto1.setTotalIntake(model.getTotalIntake());
        if (ObjectUtils.isNotEmpty(model.getBalance())) dto1.setBalance(model.getBalance());
        if (ObjectUtils.isNotEmpty(model.getTotalOutput())) dto1.setTotalOutput(model.getTotalOutput());
        if (ObjectUtils.isNotEmpty(model.getUrine())) dto1.setUrine(model.getUrine());
        if (ObjectUtils.isNotEmpty(model.getTubeVomit())) dto1.setTubeVomit(model.getTubeVomit());
        if (ObjectUtils.isNotEmpty(model.getDrainFaeces())) dto1.setDrainFaeces(model.getDrainFaeces());
        if (ObjectUtils.isNotEmpty(model.getOutputType())) dto1.setOutputType(model.getOutputType());
        if (ObjectUtils.isNotEmpty(model.getOtherInformation())) dto1.setAdditionalInformation(model.getOtherInformation());

        if (ObjectUtils.isNotEmpty(model.getPatientDetail())  && ObjectUtils.isNotEmpty(model.getPatientDetail())) {
            dto1.setPatient(this.patientDetailService.mapToPatientDto(model.getPatientDetail()));
        }
        if (ObjectUtils.isNotEmpty(model.getCaptureBy()) && ObjectUtils.isNotEmpty(model.getCaptureBy().getId())){
            dto1.setCaptureBy(this.userService.mapToDtoClean(model.getCaptureBy()));
            dto1.setCaptureByName(model.getCaptureBy().getFullName());
        }
        if (ObjectUtils.isNotEmpty(model.getCapturedFrom()) && ObjectUtils.isNotEmpty(model.getCapturedFrom().getId())){
            dto1.setCapturedFrom(this.departmentService.mapModelToDto(model.getCapturedFrom()));
        }
        return dto1;
    }
}
