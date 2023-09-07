package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.modules.lab.dto.LabParameterDto;
import com.hmis.server.hmis.modules.lab.iservice.ILabParameterService;
import com.hmis.server.hmis.modules.lab.model.LabParameter;
import com.hmis.server.hmis.modules.lab.repository.LabParameterRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LabParameterServiceImpl implements ILabParameterService {
    @Autowired
    private LabParameterRepository labParameterRepository;

    public ResponseEntity<Boolean> createMany(List<LabParameter> list) {
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty Lab Parameter");
        }
        this.labParameterRepository.saveAll(list);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    public ResponseEntity<Boolean> createOne(LabParameter labParameter) {
        if (!this.isValid(labParameter)) {
            return ResponseEntity.badRequest().body(false);
        }
        this.labParameterRepository.save(labParameter);
        return ResponseEntity.ok().body(true);
    }

    private boolean isValid(LabParameter labParameter) {
        if (ObjectUtils.isEmpty(labParameter)) {
            return false;
        }
        return labParameter.getTitle() != null;
    }

    public List<LabParameter> findAll() {
        List<LabParameter> all = this.labParameterRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (all.isEmpty()) {
            return new ArrayList<>();
        }
        return all;
    }

    public ResponseEntity<Boolean> removeMany(List<LabParameter> labParameters) {
        try {
            if (!labParameters.isEmpty()) {
                for (LabParameter lab : labParameters) {
                    if (lab.getId() != null){
                        this.labParameterRepository.deleteById(lab.getId());
                    }else{
                        Optional<LabParameter> optional = this.labParameterRepository.findByTitle(lab.getTitle());
                        optional.ifPresent(labParameter -> this.labParameterRepository.delete(labParameter));
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(true);
            }
            return ResponseEntity.badRequest().body(false);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public List<LabParameterDto> searchLabParameter(String search) {
        List<LabParameterDto> dtoList = new ArrayList<>();
        List<LabParameter> list = this.labParameterRepository.findByTitleIsContainingIgnoreCase(search);
        if (list.size() > 0) {
            dtoList = list.stream().map(param -> new LabParameterDto(param.getId(), param.getTitle())).collect(Collectors.toList());
        }
        return dtoList;
    }
}
