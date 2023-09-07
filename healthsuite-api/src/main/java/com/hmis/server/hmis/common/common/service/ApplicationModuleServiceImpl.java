package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IApplicationModuleService;
import com.hmis.server.hmis.common.common.dto.ApplicationModuleDto;
import com.hmis.server.hmis.common.common.model.ApplicationModule;
import com.hmis.server.hmis.common.common.repository.ApplicationModuleRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationModuleServiceImpl implements IApplicationModuleService {

    private static final String title = " Application Module";

    @Autowired
    ApplicationModuleRepository applicationModuleRepository;

    @Override
    public List<ApplicationModuleDto> findAllApplicationModule() {
        return mapModelListToDtoList(applicationModuleRepository.findAll());
    }

    @Override
    public ApplicationModuleDto findOneApplicationModuleByName(String name) {
        return mapModelToDto(applicationModuleRepository.findByName(name));
    }

    @Override
    public ApplicationModuleDto findOneApplicationModuleByCode(String code) {
        return mapModelToDto(applicationModuleRepository.findByCode(code));

    }

    private ApplicationModule mapDtoToModel(ApplicationModuleDto applicationModuleDto){
        if (applicationModuleDto != null){
            ApplicationModule applicationModule = new ApplicationModule();
            setModel(applicationModuleDto, applicationModule);
            return applicationModule;
        }else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR+title);
    }

    public ApplicationModuleDto mapModelToDto(ApplicationModule applicationModule){
        if (applicationModule != null){
            ApplicationModuleDto applicationModuleDto = new ApplicationModuleDto();
            setDto(applicationModuleDto, applicationModule);
            return applicationModuleDto;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR+title);
    }

    private List<ApplicationModule> mapDtoListToModelList(List<ApplicationModuleDto> applicationModuleDtoList){
        if (applicationModuleDtoList != null){
            List<ApplicationModule> applicationModules = new ArrayList<>();
            applicationModuleDtoList.forEach(applicationModuleDto -> {
                ApplicationModule applicationModule = new ApplicationModule();
                setModel(applicationModuleDto,  applicationModule);
                applicationModules.add(applicationModule);
            });
            return applicationModules;

        }else throw new HmisApplicationException("Empty ApplicationDtoList"+title);
    }

    private void setModel(ApplicationModuleDto applicationModuleDto, ApplicationModule applicationModule) {
        if (applicationModuleDto.getId().isPresent())
            applicationModule.setId(applicationModuleDto.getId().get());
        if (applicationModuleDto.getModuleName().isPresent())
            applicationModule.setName(applicationModuleDto.getModuleName().get());
        if(applicationModuleDto.getModuleCode().isPresent())
            applicationModule.setCode(applicationModuleDto.getModuleCode().get());
    }

    private List<ApplicationModuleDto> mapModelListToDtoList(List<ApplicationModule> applicationModuleList){

        if (applicationModuleList != null ){
            List<ApplicationModuleDto> applicationModuleDtoList = new ArrayList<>();
            applicationModuleList.forEach(applicationModule -> {
                ApplicationModuleDto applicationModuleDto = new ApplicationModuleDto();
                setDto(applicationModuleDto,   applicationModule );
                applicationModuleDtoList.add(applicationModuleDto);
            });
            return applicationModuleDtoList;
        } else throw new HmisApplicationException("Empty ApplicationModelList"+title);
    }

    private void setDto(ApplicationModuleDto applicationModuleDto, ApplicationModule applicationModule) {
        if (applicationModule.getId() != null)
            applicationModuleDto.setId(Optional.of(applicationModule.getId()));
        if(applicationModule.getName() != null)
            applicationModuleDto.setModuleName(Optional.of(applicationModule.getName()));
        if (applicationModule.getCode() != null)
            applicationModuleDto.setModuleCode(Optional.of(applicationModule.getCode()));
    }


}
