package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.dto.*;
import com.hmis.server.hmis.modules.clearking.iservice.IClerkingTemplateService;
import com.hmis.server.hmis.modules.clearking.model.ClerkGeneralClerkDesk;
import com.hmis.server.hmis.modules.clearking.model.ClerkingDoctorTemplate;
import com.hmis.server.hmis.modules.clearking.model.ClerkingGeneralOutPatientDesk;
import com.hmis.server.hmis.modules.clearking.repository.DoctorClerkingTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.CLERKING_TEMPLATE_PREFIX;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.CLERKING_TEMPLATE_CODE_PREFIX;


@Service
@Slf4j
public class DoctorClerkingTemplateServiceImpl implements IClerkingTemplateService {
    private final DoctorClerkingTemplateRepository repository;
    private final ClerkingGeneralOutPatientDeskServiceImpl outPatientDeskService;
    private final CommonService commonService;

    public DoctorClerkingTemplateServiceImpl(
            DoctorClerkingTemplateRepository repository,
            @Lazy ClerkingGeneralOutPatientDeskServiceImpl outPatientDeskService,
            @Lazy CommonService commonService
    ) {
        this.repository = repository;
        this.outPatientDeskService = outPatientDeskService;
        this.commonService = commonService;
    }

    @Override
    public ResponseEntity<Boolean> saveGeneralOutPatientDeskTemplate(ClerkingGeneralOutPatientDesk desk, String templateName) {
        try {
            ClerkingDoctorTemplate template = new ClerkingDoctorTemplate();
            template.setDeskEnum(ClinicDeskEnum.GENERAL_OUTPATIENT_DESK);
            template.setSavedBy(desk.getCapturedBy());
            template.setOutPatientDesk(desk);
            template.setTemplateName(templateName);
            template.setCode(this.generateTemplateCode());
            this.repository.save(template);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(false);
        }
    }

    public void saveGeneralClerkingDeskTemplate(ClerkGeneralClerkDesk desk, String templateName) {
        try{
            ClerkingDoctorTemplate template = new ClerkingDoctorTemplate();
            template.setDeskEnum(ClinicDeskEnum.GENERAL_CLERKING_DESK);
            template.setSavedBy(desk.getClerkedBy());
            template.setGeneralClerkDesk(desk);
            template.setTemplateName(templateName);
            template.setCode(this.generateTemplateCode());
            this.repository.save(template);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @Override
    public List<ClerkingTemplateDto> searchDoctorTemplate(String term, Long doctorId, ClinicDeskEnum deskEnum){
        List<ClerkingTemplateDto> dtoList = new ArrayList<>();
        List<ClerkingDoctorTemplate> list = this.repository.findAllByTemplateNameContainsIgnoreCaseAndSavedByAndDeskEnum(term, new User(doctorId), deskEnum);
        if (list.size()>0){
            dtoList = list.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public ClerkingDoctorTemplate findOne(Long id){
        Optional<ClerkingDoctorTemplate> optional = this.repository.findById(id);
        if (!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    @Override
    public OutPatientDeskDto findOutPatientDeskTemplate(Long templateId){
        ClerkingDoctorTemplate template = this.findOne(templateId);
        if (ObjectUtils.isEmpty(template.getOutPatientDesk())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template Was Not Properly Saved");
        }
        OutPatientDeskDto deskDto = this.outPatientDeskService.mapOutPatientDeskModelToDto(template.getOutPatientDesk());
        deskDto.setHasInformantDetails(false);
        deskDto.setInformantDetail(new InformantDetailsDto());
        deskDto.setPatient(null);
        deskDto.setId(null);
        return deskDto;
    }


    public GeneralClerkDeskDto findGeneralClerkDeskTemplate(Long templateId) {
        ClerkingDoctorTemplate template = this.findOne(templateId);
        if (template.getGeneralClerkDesk() == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template Was Not Properly Saved");
        }
        return this.outPatientDeskService.mapGeneralClerkModelToDto(template.getGeneralClerkDesk());
    }

    private ClerkingTemplateDto mapToDto(ClerkingDoctorTemplate model){
        ClerkingTemplateDto dto = new ClerkingTemplateDto();
        if (ObjectUtils.isNotEmpty(model.getId())){
            dto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getTemplateName())){
            dto.setTemplateName(model.getTemplateName());
        }
        if (ObjectUtils.isNotEmpty(model.getDeskEnum())){
            dto.setDeskEnum(model.getDeskEnum().name());
            dto.setDeskTitle(model.getDeskEnum().name().replace("_", " "));
        }
        if (ObjectUtils.isNotEmpty(model.getSavedDate())){
            dto.setSavedDate(model.getSavedDate());
        }
        if (ObjectUtils.isNotEmpty(model.getSavedTime())){
            dto.setSavedTime(model.getSavedTime());
        }
        if (ObjectUtils.isNotEmpty(model.getCode())){
            dto.setCode(model.getCode());
        }
        return dto;
    }

    private String generateTemplateCode() {
        GenerateCodeDto generateCodeDto = new GenerateCodeDto();
        generateCodeDto.setDefaultPrefix(CLERKING_TEMPLATE_PREFIX);
        generateCodeDto.setGlobalSettingKey(Optional.of(CLERKING_TEMPLATE_CODE_PREFIX));
        generateCodeDto.setLastGeneratedCode(this.repository.findTopByOrderByIdDesc().map(ClerkingDoctorTemplate::getCode));
        return this.commonService.generateDataCode(generateCodeDto);
    }
}
