package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.MeansOfIdentificationDto;
import com.hmis.server.hmis.common.common.model.MeansOfIdentification;

import java.util.List;

public interface IMeansOfIdentificationService {

    MeansOfIdentification createMeansOfIdentification(MeansOfIdentificationDto dto);

    MeansOfIdentification updateMeansOfIdentification(MeansOfIdentificationDto dto);

    List<MeansOfIdentification> findAll();

    MeansOfIdentification findOne(Long id);

}
