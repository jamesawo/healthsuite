package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.model.EthnicGroup;

import java.util.List;

public interface IEthnicGroupService {

    EthnicGroup createEthnicGroup(String name);

    EthnicGroup updateEthnicGroup(Long id, String name);

    List<EthnicGroup> findAllEthnicGroup();
}
