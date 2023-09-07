package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Nationality;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientContactDetailService;
import com.hmis.server.hmis.modules.emr.dto.PatientContactDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientContactDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientContactDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientContactDetailServiceImpl implements IPatientContactDetailService {
    @Autowired
    private PatientContactDetailRepository repository;


    @Override
    public PatientContactDetail createPatientContactDetail(PatientContactDetailDto contactDetailDto) {
        PatientContactDetail contactDetail = new PatientContactDetail();
        try{
            this.setModel(contactDetailDto, contactDetail);
            return this.repository.save(contactDetail);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    @Override
    public PatientContactDetail updatePatientContactDetail(PatientContactDetailDto contactDetailDto) {
        if (!contactDetailDto.getId().isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide Patient Contact Detail ID ");
        }
        try{
            PatientContactDetail contactDetail = findPatientContactDetail(contactDetailDto.getId().get());
            this.setModel(contactDetailDto, contactDetail);
            return this.repository.save(contactDetail);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }

    }

    @Override
    public PatientContactDetail findPatientContactDetail(Long id) {
        Optional<PatientContactDetail> contactDetail = this.repository.findById(id);
        if (!contactDetail.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient Contact Detail: "+ id);
        }
        return contactDetail.get();
    }

    @Override
    public void removePatientContactDetail(Long id) {
        try{
            this.repository.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setModel(PatientContactDetailDto dto, PatientContactDetail detail){

        if (dto.getEmail() != null ){
            detail.setEmail(dto.getEmail());
        }
        if (dto.getNationalityId() != null){
            detail.setNationality(new Nationality(dto.getNationalityId()));
        }
        if (dto.getPhoneNumber() != null){
            detail.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getResidentialAddress() != null){
            detail.setResidentialAddress(dto.getResidentialAddress());
        }
    }
}
