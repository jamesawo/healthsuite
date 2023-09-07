package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.settings.service.LocationSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/locations/")
public class LocationSettingsController {

    @Autowired
    LocationSettingService locationSettingService;

    @GetMapping("all")
    public ResponseDto getAllLocation(){
        return locationSettingService.findAllLocation();
    }

    @GetMapping(value = "get-default")
    public ResponseEntity<DepartmentDto> getDefaultLocation() {
        return this.locationSettingService.getDefaultLocation();
    }

}
