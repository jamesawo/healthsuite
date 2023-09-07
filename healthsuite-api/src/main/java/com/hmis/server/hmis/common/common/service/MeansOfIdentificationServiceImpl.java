package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IMeansOfIdentificationService;
import com.hmis.server.hmis.common.common.dto.MeansOfIdentificationDto;
import com.hmis.server.hmis.common.common.model.MeansOfIdentification;
import com.hmis.server.hmis.common.common.repository.MeansOfIdentificationRepository;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeansOfIdentificationServiceImpl implements IMeansOfIdentificationService {

    @Autowired
    MeansOfIdentificationRepository meansOfIdentificationRepository;

    @Override
    public MeansOfIdentification createMeansOfIdentification(MeansOfIdentificationDto dto) {
        if (dto.getName() != null){
            MeansOfIdentification meansOfIdentification = new MeansOfIdentification(dto.getName());
            return this.meansOfIdentificationRepository.save(meansOfIdentification);
        }else{
            throw new HmisApplicationException("Provide title");
        }
    }

    @Override
    public MeansOfIdentification updateMeansOfIdentification(MeansOfIdentificationDto dto) {
        if (!dto.getId().isPresent()){
            throw new HmisApplicationException("Provide id");
        }
        MeansOfIdentification identification = this.findOne(dto.getId().get());
        identification.setName(dto.getName());
        return this.meansOfIdentificationRepository.save(identification);
    }

    @Override
    public List<MeansOfIdentification> findAll() {
        return this.meansOfIdentificationRepository.findAll();
    }

    @Override
    public MeansOfIdentification findOne(Long id) {
        Optional<MeansOfIdentification> meansOfIdentification = this.meansOfIdentificationRepository.findById(id);
        if(!meansOfIdentification.isPresent()){
            throw new HmisApplicationException("Nothing found");
        }
        return meansOfIdentification.get();
    }
}
