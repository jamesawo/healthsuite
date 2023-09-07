package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleDto;

import java.util.List;

public interface IApplicationModuleService {

    List<ApplicationModuleDto> findAllApplicationModule();

    ApplicationModuleDto findOneApplicationModuleByName(String name);

    ApplicationModuleDto findOneApplicationModuleByCode(String code);



}
