package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleDto;
import com.hmis.server.hmis.common.common.dto.DepartmentCategoryDto;
import com.hmis.server.hmis.common.common.dto.LocationMapDto;
import com.hmis.server.hmis.common.common.model.LocationMap;
import com.hmis.server.hmis.common.user.dto.UserDto;

import java.util.List;

public interface ILocationMapService {

    List<LocationMapDto> findAllLocation();

    LocationMapDto createOneLocation(LocationMapDto locationMapDto);

    LocationMapDto setUserLocation(LocationMapDto locationMapDto, UserDto userDto);

    void rememberUserLocation(LocationMapDto locationMapDto, UserDto userDto);

    void updateUserLocation(LocationMapDto locationMapDto, UserDto userDto);

    boolean isLocationAllowed(LocationMapDto locationMapDto);

    LocationMapDto findUserLocationHistory(LocationMapDto locationMapDto, UserDto userDto);

    LocationMap findLocationMapByDepartmentCategory(DepartmentCategoryDto departmentCategoryDto);

    LocationMap findLocationMapByApplicationModule(ApplicationModuleDto applicationModuleDto);
}
