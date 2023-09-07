package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IDiseaseService;
import com.hmis.server.hmis.common.common.dto.DiseaseDto;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.model.Disease;
import com.hmis.server.hmis.common.common.repository.DiseaseRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiseaseServiceImpl implements IDiseaseService {
    @Autowired
    private DiseaseRepository diseaseRepository;
    @Autowired
    private CommonService commonService;

    @Override
    public DiseaseDto createOne(DiseaseDto dto){
        dto.setCode(this.generateCode());
        Disease saved = this.diseaseRepository.save(this.mapToModel(dto));
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public List<DiseaseDto> findAll(){
        List<DiseaseDto> diseaseDtoList = new ArrayList<>();
        List<Disease> diseaseList = this.diseaseRepository.findAll();
        if (!diseaseList.isEmpty()){
            diseaseDtoList = diseaseList.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return diseaseDtoList;
    }

    @Override
    public DiseaseDto findOne(Long id){
        Optional<Disease> optionalDisease = this.diseaseRepository.findById(id);
        if (!optionalDisease.isPresent()){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disease Not Found");
        }
        return  this.mapToDto(optionalDisease.get());
    }

    @Override
    public Disease findOneRaw(Long id){
        Optional<Disease> optionalDisease = this.diseaseRepository.findById(id);
        if (!optionalDisease.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disease Not Found");
        }
        return optionalDisease.get();
    }

    private DiseaseDto mapToDto(Disease model){
        DiseaseDto dto = new DiseaseDto();
        if (ObjectUtils.isEmpty(model.getId())){
            dto.setId(model.getId());
        }
        if (ObjectUtils.isNotEmpty(model.getCode())){
            dto.setCode(model.getCode());
        }
        if (ObjectUtils.isNotEmpty(model.getName())){
            dto.setName(model.getName());
        }
        return dto;
    }

    private Disease mapToModel(DiseaseDto dto){
        Disease model = new Disease();
        if(ObjectUtils.isNotEmpty(dto.getId())){
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getName())){
            model.setName(dto.getName());
        }
        if (ObjectUtils.isNotEmpty(dto.getCode())){
            model.setCode(dto.getCode());
        }
        return model;
    }

    private String generateCode(){
        GenerateCodeDto codeDto = new GenerateCodeDto();
        codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.DISEASE_CODE_PREFIX));
        codeDto.setDefaultPrefix(HmisCodeDefaults.DISEASE_CODE_PREFIX_DEFAULT);
        codeDto.setLastGeneratedCode(this.diseaseRepository.findTopByOrderByIdDesc().map(Disease::getCode));
        return commonService.generateDataCode(codeDto);
    }
}
