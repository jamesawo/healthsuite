package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IEthnicGroupService;
import com.hmis.server.hmis.common.common.model.EthnicGroup;
import com.hmis.server.hmis.common.common.repository.EthnicGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EthnicGroupServiceImpl implements IEthnicGroupService {

    @Autowired
    EthnicGroupRepository ethnicGroupRepository;

    @Override
    public EthnicGroup createEthnicGroup(String name) {

        return this.ethnicGroupRepository.save( new EthnicGroup(name) );
    }

    @Override
    public EthnicGroup updateEthnicGroup( Long id, String name ) {

        return this.ethnicGroupRepository.save( new EthnicGroup( id, name ) );
    }

    @Override
    public List<EthnicGroup> findAllEthnicGroup() {
        return this.ethnicGroupRepository.findAll();
    }
}
