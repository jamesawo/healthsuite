package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.ILocationMapService;
import com.hmis.server.hmis.common.common.dto.*;
import com.hmis.server.hmis.common.common.model.ApplicationModule;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.model.LocationMap;
import com.hmis.server.hmis.common.common.repository.LocationMapRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.common.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.constant.HmisConstant.DEFAULT_DEPARTMENT;

@Service
public class LocationMapServiceImpl implements ILocationMapService {

    @Autowired
    LocationMapRepository locationMapRepository;

    @Autowired
    DepartmentServiceImpl departmentService;

    @Autowired
    DepartmentCategoryServiceImpl departmentCategoryService;

    @Autowired
    ApplicationModuleServiceImpl applicationModuleService;


    @Override
    public List<LocationMapDto> findAllLocation() {

        List<LocationMapDto> locationMapDtoList = new ArrayList<>();
        /* get a list of department extract the necessary info to locationMapDto */
        List<DepartmentDto> departmentDtoList = departmentService.findAllDepartment();
        // todo:: will allow user to
        // create department category
        // and map the department category to application module

        if (!departmentDtoList.isEmpty()) {
            for (DepartmentDto departmentDto : departmentDtoList) {

                LocationMapDto locationMapDto = new LocationMapDto();

                locationMapDto.setDepartmentName(departmentDto.getName());
                locationMapDto.setDepartmentCode(departmentDto.getCode());

                LocationObject locationObject = new LocationObject();
                locationObject.setDepartmentCategory(departmentDto.getDepartmentCategory());


                if (departmentDto.getDepartmentCategory().isPresent()) {
                    LocationMap locationMapByDepartmentCategory = findLocationMapByDepartmentCategory(
                            departmentDto.getDepartmentCategory().get());

                    locationObject.setAllocatedModule(Optional.ofNullable(
                            applicationModuleService.mapModelToDto(
                                    locationMapByDepartmentCategory.getApplicationModule()))
                    );
                }
                locationMapDto.setLocation(Optional.of(locationObject));

                locationMapDtoList.add(locationMapDto);
            }
        }

        return locationMapDtoList;
    }

    public DepartmentDto findDefaultLocation() {
        Department department = this.departmentService.findByName(DEFAULT_DEPARTMENT);
        return this.departmentService.mapModelToDto(department);
    }

    @Override
    public LocationMapDto createOneLocation(LocationMapDto locationMapDto) {
        return null;
    }

    @Override
    public LocationMapDto setUserLocation(LocationMapDto locationMapDto, UserDto userDto) {
        return null;
    }

    @Override
    public void rememberUserLocation(LocationMapDto locationMapDto, UserDto userDto) {
    }


    @Override
    public void updateUserLocation(LocationMapDto locationMapDto, UserDto userDto) {

    }

    @Override
    public boolean isLocationAllowed(LocationMapDto locationMapDto) {
        return false;
    }

    @Override
    public LocationMapDto findUserLocationHistory(LocationMapDto locationMapDto, UserDto userDto) {
        return null;
    }

    @Override
    public LocationMap findLocationMapByDepartmentCategory(DepartmentCategoryDto departmentCategoryDto) {
        if (departmentCategoryDto.getId().isPresent()) {
            Optional<LocationMap> byDepartmentCategory = locationMapRepository.findByDepartmentCategory(
                    new DepartmentCategory(departmentCategoryDto.getId().get()));
            if (byDepartmentCategory.isPresent()) return byDepartmentCategory.get();
            else throw new HmisApplicationException("Cannot Find LocationMap Record With DeptCat ID: " +
                    departmentCategoryDto.getId().get());
        } else
            throw new HmisApplicationException(HmisExceptionMessage.ID_NOT_PROVIDED + "Find Location By DepCategory");
    }

    @Override
    public LocationMap findLocationMapByApplicationModule(ApplicationModuleDto applicationModuleDto) {
        if (applicationModuleDto.getId().isPresent()) {
            LocationMap byApplicationModule = locationMapRepository.findByApplicationModule(
                    new ApplicationModule(applicationModuleDto.getId().get()));
            if (byApplicationModule != null) return byApplicationModule;
            else throw new HmisApplicationException(HmisExceptionMessage.NOTHING_FOUND);
        } else throw new HmisApplicationException(HmisExceptionMessage.ID_NOT_PROVIDED);
    }

    private LocationMap mapDtoToModel(LocationMapDto locationMapDto) {
        return null;
    }

    private LocationMapDto mapModelToDto(LocationMap locationMap) {
        return null;
    }

    private List<LocationMap> mapDtoListToModelList(List<LocationMapDto> locationMapDtoList) {
        return null;
    }

    private List<LocationMapDto> mapModelListToDtoList(List<LocationMap> locationMapList) {
        return null;
    }
}
