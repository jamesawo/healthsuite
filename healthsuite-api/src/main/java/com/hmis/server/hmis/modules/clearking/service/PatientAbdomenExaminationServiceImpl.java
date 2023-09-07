package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.AbdomenFormDto;
import com.hmis.server.hmis.modules.clearking.dto.YesNoEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkAbdomenExaminationDetails;
import com.hmis.server.hmis.modules.clearking.repository.PatientAbdomenExaminationRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientAbdomenExaminationServiceImpl {
    @Autowired
    private PatientAbdomenExaminationRepository repository;

    public ClerkAbdomenExaminationDetails save(AbdomenFormDto abdomenForm) {
        ClerkAbdomenExaminationDetails model = this.mapToModel(abdomenForm);
        return this.repository.save(model);
    }

    public ClerkAbdomenExaminationDetails findById(Long id) {
        Optional<ClerkAbdomenExaminationDetails> optional = this.repository.findById(id);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    public ClerkAbdomenExaminationDetails mapToModel(AbdomenFormDto dto) {
        ClerkAbdomenExaminationDetails model = new ClerkAbdomenExaminationDetails();

        if (ObjectUtils.isNotEmpty(dto.getId())) {
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getMovesWithRespiration())) {
            model.setMovesWithRespiration(dto.getMovesWithRespiration().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getDistended())) {
            model.setDistended(dto.getDistended().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getVisiblePeripheralVein())) {
            model.setVisiblePeripheralVein(dto.getVisiblePeripheralVein().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getScarificationMarks())) {
            model.setScarificationMarks(dto.getScarificationMarks().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getShape())) {
            model.setShape(dto.getShape());
        }
        if (ObjectUtils.isNotEmpty(dto.getHanialOrificesIntact())) {
            model.setHanialOrificesIntact(dto.getHanialOrificesIntact().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getPalpationLiverEnlargement())) {
            model.setPalpationLiverEnlargement(dto.getPalpationLiverEnlargement().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getPalpationKidneyEnlargement())) {
            model.setPalpationKidneyEnlargement(dto.getPalpationKidneyEnlargement().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getPalpationSpleenEnlargement())) {
            model.setPalpationSpleenEnlargement(dto.getPalpationSpleenEnlargement().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getPalpationOtherMasses())) {
            model.setPalpationOtherMasses(dto.getPalpationOtherMasses().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getAscultations())) {
            model.setAscultationsBruits(dto.getAscultations().name());
        }
        if (ObjectUtils.isNotEmpty(dto.getAscitis())) {
            model.setAscitis(dto.getAscitis().name());
        }
        return model;
    }

    public AbdomenFormDto mapToDto(ClerkAbdomenExaminationDetails model) {
        AbdomenFormDto formDto = new AbdomenFormDto();

        if (ObjectUtils.isNotEmpty(model.getId())) {
            formDto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getMovesWithRespiration())) {
            formDto.setMovesWithRespiration(YesNoEnum.valueOf(model.getMovesWithRespiration()));
        }
        if (ObjectUtils.isNotEmpty(model.getDistended())) {
            formDto.setDistended(YesNoEnum.valueOf(model.getDistended()));
        }
        if (ObjectUtils.isNotEmpty(model.getVisiblePeripheralVein())) {
            formDto.setVisiblePeripheralVein(YesNoEnum.valueOf(model.getVisiblePeripheralVein()));
        }
        if (ObjectUtils.isNotEmpty(model.getScarificationMarks())) {
            formDto.setScarificationMarks(YesNoEnum.valueOf(model.getScarificationMarks()));
        }
        if (ObjectUtils.isNotEmpty(model.getShape())) {
            formDto.setShape(model.getShape());
        }
        if (ObjectUtils.isNotEmpty(model.getHanialOrificesIntact())) {
            formDto.setHanialOrificesIntact(YesNoEnum.valueOf(model.getHanialOrificesIntact()));
        }
        if (ObjectUtils.isNotEmpty(model.getPalpationLiverEnlargement())) {
            formDto.setPalpationLiverEnlargement(YesNoEnum.valueOf(model.getPalpationLiverEnlargement()));
        }
        if (ObjectUtils.isNotEmpty(model.getPalpationKidneyEnlargement())) {
            formDto.setPalpationKidneyEnlargement(YesNoEnum.valueOf(model.getPalpationKidneyEnlargement()));
        }
        if (ObjectUtils.isNotEmpty(model.getPalpationSpleenEnlargement())) {
            formDto.setPalpationSpleenEnlargement(YesNoEnum.valueOf(model.getPalpationSpleenEnlargement()));
        }
        if (ObjectUtils.isNotEmpty(model.getPalpationOtherMasses())) {
            formDto.setPalpationOtherMasses(YesNoEnum.valueOf(model.getPalpationOtherMasses()));
        }
        if (ObjectUtils.isNotEmpty(model.getAscultationsBruits())) {
            formDto.setAscultations(YesNoEnum.valueOf(model.getAscultationsBruits()));
        }
        if (ObjectUtils.isNotEmpty(model.getAscitis())) {
            formDto.setAscitis(YesNoEnum.valueOf(model.getAscitis()));
        }
        return formDto;
    }
}
