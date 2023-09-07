package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientOtherDetailService;
import com.hmis.server.hmis.modules.emr.dto.PatientOtherDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientOtherDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientOtherDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientOtherDetailServiceImpl implements IPatientOtherDetailService {

    @Autowired
    private PatientOtherDetailRepository repository;

    @Override
    public PatientOtherDetail createPatientOtherDetail(PatientOtherDetailDto dto) {
        PatientOtherDetail patientOtherDetail = new PatientOtherDetail();
        patientOtherDetail.setAccessToCleanWater(dto.getAccessToCleanWater());
        patientOtherDetail.setAccessToElectricity(dto.getAccessToElectricity());
        patientOtherDetail.setAccessToGoodFood(dto.getAccessToGoodFood());
        patientOtherDetail.setAccessToInternet(dto.getAccessToInternet());
        patientOtherDetail.setAccessToTelephone(dto.getAccessToTelephone());
        return repository.save(patientOtherDetail);
    }

    @Override
    public PatientOtherDetail updatePatientOtherDetail(PatientOtherDetailDto dto) {
        if (!dto.getId().isPresent()){
            throw new HmisApplicationException(HmisExceptionMessage.ID_NOT_PROVIDED + "patient other details");
        }

        PatientOtherDetail patientOtherDetail = this.findPatientOtherDetail(dto.getId().get());
        patientOtherDetail.setAccessToTelephone(dto.getAccessToTelephone());
        patientOtherDetail.setAccessToInternet(dto.getAccessToInternet());
        patientOtherDetail.setAccessToGoodFood(dto.getAccessToGoodFood());
        patientOtherDetail.setAccessToElectricity(dto.getAccessToElectricity());
        patientOtherDetail.setAccessToCleanWater(dto.getAccessToCleanWater());
        return this.repository.save(patientOtherDetail);
    }

    @Override
    public PatientOtherDetail findPatientOtherDetail(Long id) {
        Optional<PatientOtherDetail> patientOtherDetail = this.repository.findById(id);
        if (!patientOtherDetail.isPresent()){
            throw new HmisApplicationException(HmisExceptionMessage.NOTHING_FOUND);
        }
        return patientOtherDetail.get();
    }

    @Override
    public void removePatientOtherDetail(Long id) {

    }
}
