package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.modules.emr.IService.IPatientNumberService;
import com.hmis.server.hmis.modules.emr.dto.PatientNumberDto;
import com.hmis.server.hmis.modules.emr.model.PatientNumber;
import com.hmis.server.hmis.modules.emr.repository.PatientNumberRepository;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.*;

@Service
public class PatientNumberServiceImpl implements IPatientNumberService {

    @Autowired
    private PatientNumberRepository patientNumberRepository;

    @Autowired
    private GlobalSettingsImpl globalSettingsService;

    @Override
    public PatientNumber createOne() {
        // check if table is empty
        String pNumberPrefix = globalSettingsService
                .findByKey(new GlobalSettingsDto(Optional.of(PATIENT_NUMBER_PREFIX))).getValue()
                .orElse("");
        String pNumberSuffix = globalSettingsService
                .findByKey(new GlobalSettingsDto(Optional.of(PATIENT_NUMBER_SUFFIX))).getValue()
                .orElse("");
        String pNumberStartPoint = globalSettingsService
                .findByKey(new GlobalSettingsDto(Optional.of(APP_CODE_START_NUMBER))).getValue()
                .orElse(String.valueOf(HmisCodeDefaults.APP_CODE_START_NUMBER_DEFAULT));

        //find last patient number created
        Optional<PatientNumber> patientNumber = Optional.ofNullable(patientNumberRepository.findTopByOrderByIdDesc());

        if (patientNumber.isPresent()){
            PatientNumber newPatientNumber = new PatientNumber();
            newPatientNumber.setNumber(new AtomicInteger( patientNumber.get().getNumber().incrementAndGet()) );
            newPatientNumber.setPrefix(pNumberPrefix);
            newPatientNumber.setSuffix(pNumberSuffix);
            newPatientNumber.setIsAllocated(false);
            return patientNumberRepository.save(newPatientNumber);
        }else{
            PatientNumber newPatientNumber = new PatientNumber();
            newPatientNumber.setNumber(new AtomicInteger( Integer.parseInt(pNumberStartPoint) ) );
            newPatientNumber.setPrefix(pNumberPrefix);
            newPatientNumber.setSuffix(pNumberSuffix);
            newPatientNumber.setIsAllocated(false);
            return patientNumberRepository.save(newPatientNumber);
        }

    }


    @Override
    public PatientNumber findOne(PatientNumberDto patientNumberDto) throws EntityNotFoundException, BadRequestException {
        if (patientNumberDto.getId().isPresent() ){
            Optional<PatientNumber> patientNumber = patientNumberRepository.findById(patientNumberDto.getId().get());
            if (patientNumber.isPresent()){
                return patientNumber.get();
            }else {
                throw new EntityNotFoundException(HmisConstant.STATUS_404, "cannot find patient number ");
            }
        }else{
            throw new BadRequestException(HmisConstant.STATUS_400, "no patient number was provided");
        }
    }

    //not in use
    @Override
    public void allocateNumber(PatientNumberDto patientNumberDto)  {
        try{
            PatientNumber patientNumber = this.findOne(patientNumberDto);
            patientNumber.setIsAllocated(true);
            patientNumberRepository.save(patientNumber);
        }catch (Exception e){
            throw new HmisApplicationException(e.getMessage());
        }
    }

    //not in use
    @Override
    public boolean isAllocated(PatientNumberDto patientNumberDto) {
        return false;
    }

    @Override
    public String generatePatientNumber() {
        PatientNumber patientNumber = this.createOne();
        return patientNumber.getPrefix() + patientNumber.getNumber() + patientNumber.getSuffix();
    }
}
