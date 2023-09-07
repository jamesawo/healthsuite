package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.DoctorDiseaseLogDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkDoctorDiseaseLog;
import com.hmis.server.hmis.modules.clearking.repository.DoctorDiseaseLogRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class DoctorDiseaseLogServiceImpl {
    @Autowired
    private DoctorDiseaseLogRepository diseaseLogRepository;

    public ClerkDoctorDiseaseLog mapToModel(DoctorDiseaseLogDto dto){
        ClerkDoctorDiseaseLog model = new ClerkDoctorDiseaseLog();

        if (ObjectUtils.isNotEmpty(dto.getId())){
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

    public ClerkDoctorDiseaseLog findById(Long id){
        Optional<ClerkDoctorDiseaseLog> optional = this.diseaseLogRepository.findById(id);
        if (!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }
}
