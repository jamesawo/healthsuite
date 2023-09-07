package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.MeansOfIdentification;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientMeansOfIdentificationService;
import com.hmis.server.hmis.modules.emr.dto.PatientMeansOfIdentificationDto;
import com.hmis.server.hmis.modules.emr.model.PatientMeansOfIdentification;
import com.hmis.server.hmis.modules.emr.repository.PatientMeansOfIdentificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientMeansOfIdentificationServiceImpl implements IPatientMeansOfIdentificationService {

    @Autowired
     private HmisUtilService hmisUtilService;

    @Autowired
    private PatientMeansOfIdentificationRepository repository;

    @Override
    public PatientMeansOfIdentification createPatientMeansOfIdentification(PatientMeansOfIdentificationDto dto) {
        try{
            PatientMeansOfIdentification model = new PatientMeansOfIdentification();
            this.setModel(dto, model);
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public PatientMeansOfIdentification updatePatientMeansOfIdentification(PatientMeansOfIdentificationDto dto) {
        if (!dto.getId().isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisExceptionMessage.ID_NOT_PROVIDED + ": Patient Means of Identification.");
        }
        try{
            PatientMeansOfIdentification model = this.findPatientMeansOfIdentification(dto.getId().get());
            this.setModel(dto, model);
            return this.repository.save(model);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public PatientMeansOfIdentification findPatientMeansOfIdentification(Long id) {
        Optional<PatientMeansOfIdentification> identification = this.repository.findById(id);
        if (!identification.isPresent()){
            throw new HmisApplicationException(HmisExceptionMessage.NOTHING_FOUND+ "means of id");
        }
        return identification.get();
    }

    @Override
    public void removePatientMeansOfIdentification(Long id) {
        try{
            this.repository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void setModel(PatientMeansOfIdentificationDto dto, PatientMeansOfIdentification model){
        if (dto.getExpiryDate() != null ){
            if (dto.getExpiryDate().getYear() >= 1 && dto.getExpiryDate().getMonth() >= 1 && dto.getExpiryDate().getDay() >= 1) {
                model.setExpiryDate( this.hmisUtilService.transformToLocalDate( dto.getExpiryDate() ) );
            }
        }
        if (dto.getIdentificationType() != null)
            model.setIdentificationTypeId(new MeansOfIdentification(dto.getIdentificationType()));
        if (dto.getIdentificationNumber() != null)
            model.setIdentificationNumber(dto.getIdentificationNumber());
    }
}
