package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Relationship;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientNOKDetailService;
import com.hmis.server.hmis.modules.emr.dto.PatientNOKDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientNOKDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientNOKDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PatientNOKDetailServiceImpl implements IPatientNOKDetailService {

    @Autowired
    private PatientNOKDetailRepository repository;

    @Override
    public PatientNOKDetail createPatientNOKDetail(PatientNOKDetailDto dto) {
        try{
            PatientNOKDetail detail = new PatientNOKDetail();
            this.setModel(dto, detail);
            return this.repository.save(detail);
        }catch (Exception e){
            throw new HmisApplicationException(e.getMessage());
        }
    }

    @Override
    public PatientNOKDetail updatePatientNOKDetail(PatientNOKDetailDto dto) {
        if (!dto.getId().isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisExceptionMessage.ID_NOT_PROVIDED + "NOK Details");
        }
        try{
            PatientNOKDetail model = this.findPatientNOKDetail(dto.getId().get());
            this.setModel(dto, model);
            return this.repository.save(model);
        }catch (Exception e){
            throw new HmisApplicationException(e.getMessage());
        }
    }

    private void setModel(PatientNOKDetailDto dto, PatientNOKDetail model) {
        if (dto.getAddress() != null){
            model.setAddress(dto.getAddress());
        }
        if (dto.getName() != null){
            model.setName(dto.getName());
        }
        if (dto.getPhone() != null){
            model.setPhone(dto.getPhone());
        }
        if(dto.getRelationshipId() != null){
            model.setRelationship( new Relationship(dto.getRelationshipId()) );
        }
    }

    @Override
    public PatientNOKDetail findPatientNOKDetail(Long id) {
        Optional<PatientNOKDetail> detail = this.repository.findById(id);
        if (!detail.isPresent()){
            throw new HmisApplicationException(HmisExceptionMessage.NOTHING_FOUND + "nok details");
        }
        return detail.get();
    }


    @Override
    public void removePatientNOKDetail(Long id) {
        try{
            this.repository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
