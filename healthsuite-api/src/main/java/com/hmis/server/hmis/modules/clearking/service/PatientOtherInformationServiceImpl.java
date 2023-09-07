package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.OtherInformationDto;
import com.hmis.server.hmis.modules.clearking.model.ClerkOtherInformation;
import com.hmis.server.hmis.modules.clearking.repository.PatientOtherInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientOtherInformationServiceImpl {
    @Autowired
    private PatientOtherInformationRepository repository;

    public ClerkOtherInformation save(OtherInformationDto dto){
        try{
            return this.repository.save(new ClerkOtherInformation(dto.getOtherInfo()));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public OtherInformationDto mapToDto(ClerkOtherInformation model){
        OtherInformationDto dto = new OtherInformationDto();
        dto.setId(model.getId());
        dto.setOtherInfo(model.getOtherInformation());
        return dto;
    }
}
