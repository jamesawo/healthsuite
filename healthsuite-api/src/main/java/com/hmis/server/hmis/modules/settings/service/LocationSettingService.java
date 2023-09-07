package com.hmis.server.hmis.modules.settings.service;

import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.LocationMapServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Service
public class LocationSettingService {
    @Autowired
    CommonService commonService;

    @Autowired
    LocationMapServiceImpl locationMapService;


    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ResponseDto findAllLocation() {
        ResponseDto responseDto = new ResponseDto();
        final String message = " Find All Point Location ";
        try {
            responseDto.setData(locationMapService.findAllLocation());
            responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
            responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
            responseDto.setHttpStatusText(HmisConstant.STATUS_200);
            LOGGER.debug(commonService.getLoggerMessage(message, HmisConstant.LOGGER_TYPE_PASSED));

        } catch (Exception e) {
            responseDto.setHttpStatusText(Arrays.toString(e.getStackTrace()));
            responseDto.setMessage(e.getMessage());
            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
        }
        return responseDto;
    }

    public ResponseEntity<DepartmentDto> getDefaultLocation() {
        try {
            DepartmentDto dto = this.locationMapService.findDefaultLocation();
            return ResponseEntity.ok().body(dto);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
