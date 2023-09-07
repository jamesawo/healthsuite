package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;
import com.hmis.server.hmis.modules.emr.dto.PatientNumberDto;
import com.hmis.server.hmis.modules.emr.model.PatientNumber;

public interface IPatientNumberService {

    PatientNumber createOne();


    PatientNumber findOne(PatientNumberDto patientNumberDto) throws EntityNotFoundException, BadRequestException;

    void allocateNumber(PatientNumberDto patientNumberDto) throws BadRequestException, EntityNotFoundException;

    boolean isAllocated(PatientNumberDto patientNumberDto);

    String generatePatientNumber();
}
