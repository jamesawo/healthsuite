package com.hmis.server.hmis.common.common.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class LocationObject {

    /* This pojo will be used in LocationMapDto to map departmentCategory to applicationModule
    * for front end in set locations under the settings modules
    * */
    private Optional<DepartmentCategoryDto> departmentCategory;

    private Optional<ApplicationModuleDto> allocatedModule;
}
