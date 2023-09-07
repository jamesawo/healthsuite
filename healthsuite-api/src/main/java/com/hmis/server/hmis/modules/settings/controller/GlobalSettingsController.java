package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import com.hmis.server.hmis.modules.settings.service.GlobalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(HmisConstant.API_PREFIX +"/settings/")
public class GlobalSettingsController {
    @Autowired
    GlobalSettingsService globalSettingsService;

    @GetMapping("all")
    public ResponseDto findAll() {
        return globalSettingsService.findAll();
    }

    @PostMapping("update")
    public ResponseDto updateSettings(@RequestBody GlobalSettingsDto globalSettingsDto) {
        return globalSettingsService.updateFromMap(globalSettingsDto);
    }

    @GetMapping("settings-map")
    public ResponseDto findAllRaw() {
        return globalSettingsService.findSettingsMap();
    }

	@GetMapping( "find-by-key" )
	public ResponseDto findByKey(@RequestParam String key) {
		return globalSettingsService.findByKey(new GlobalSettingsDto(java.util.Optional.ofNullable(key)));
	}


}
