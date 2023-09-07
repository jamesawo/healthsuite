package com.hmis.server.hmis.modules.settings.service;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import com.hmis.server.hmis.modules.settings.model.GlobalSettings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingsService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final GlobalSettingsImpl globalSettings;
	private final CommonService commonService;

	@Autowired
	public GlobalSettingsService(
			GlobalSettingsImpl globalSettings,
			@Lazy CommonService commonService
	) {
		this.globalSettings = globalSettings;
		this.commonService = commonService;
	}

	public ResponseDto createOne(GlobalSettingsDto globalSettingsDto) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.createOne(globalSettingsDto));
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(e.getMessage());
			return responseDto;
		}
	}

	public ResponseDto createIfNotExist(GlobalSettingsDto dto) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.createIfNotExist(dto));
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(e.getMessage());
			return responseDto;
		}
	}

	public ResponseDto createInBatch(List< GlobalSettingsDto > globalSettingsDtoList) {
		ResponseDto responseDto = new ResponseDto();
		LOGGER.info(commonService.getAuthenticatedUser() + " is attempting to update global setting.");
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.createInBatch(globalSettingsDtoList));
			//            LOGGER.info(commonService.getAuthenticatedUser() + " attempt to update global setting was successful.");
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(commonService.getAuthenticatedUser() + " attempt to update global setting has failed.");
			LOGGER.debug(e.getMessage());
			return responseDto;
		}

	}

	public ResponseDto findAll() {
		ResponseDto responseDto = new ResponseDto();
		//        LOGGER.info(commonService.getAuthenticatedUser() + " is attempting to get all global settings.");
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.findAll());
			//            LOGGER.info(commonService.getAuthenticatedUser() + " attempt to get global setting was successful.");
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(commonService.getAuthenticatedUser() + " attempt to get global settings failed.");
			LOGGER.debug(e.getMessage());
			return responseDto;
		}
	}

	public ResponseDto findBySection(GlobalSettingsDto globalSettingsDto) {
		ResponseDto responseDto = new ResponseDto();
		//        LOGGER.info(commonService.getAuthenticatedUser() + " is attempting to get global setting by section {}.", globalSettingsDto);
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.findBySection(globalSettingsDto));
			//            LOGGER.info(commonService.getAuthenticatedUser() + " attempt to get global setting by section was successful.");
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(commonService.getAuthenticatedUser() + " attempt to get global setting by section has failed.");
			LOGGER.debug(e.getMessage());
			return responseDto;
		}
	}

	public ResponseDto findOne(GlobalSettingsDto globalSettingsDto) {
		ResponseDto responseDto = new ResponseDto();
		//        LOGGER.info(commonService.getAuthenticatedUser() + " is attempting to get one global setting {}.", globalSettingsDto);
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.findOne(globalSettingsDto));
			//            LOGGER.info(commonService.getAuthenticatedUser() + "  attempt to get one global setting was successful.");
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(commonService.getAuthenticatedUser() + " is attempting to get one global setting has failed.");
			LOGGER.debug(e.getMessage());
			return responseDto;
		}
	}

	public ResponseDto findByKey(GlobalSettingsDto globalSettingsDto) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData(globalSettings.findByKey(globalSettingsDto));
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			LOGGER.debug(commonService.getAuthenticatedUser() + " attempt to get one global setting by key failed.");
			LOGGER.debug(e.getMessage());
			return responseDto;
		}
	}

	public ResponseDto updateOne(GlobalSettingsDto globalSettingsDto) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			responseDto.setData(globalSettings.updateOne(globalSettingsDto));
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			return responseDto;
		}

	}

	public ResponseDto updateInBatch(List< GlobalSettingsDto > globalSettingsDtoList) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData(globalSettings.updateInBatch(globalSettingsDtoList));
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			return responseDto;
		}

	}

	public ResponseDto updateFromMap(GlobalSettingsDto globalSettingsDto) {
		ResponseDto responseDto = new ResponseDto();
		try {
			globalSettings.updateFromMap(globalSettingsDto);
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			return responseDto;
		}

	}

	public ResponseDto findSettingsMap() {

		ResponseDto responseDto = new ResponseDto();
		try {
			Map< String, String > map = new HashMap<>();
			List< GlobalSettings > settings = globalSettings.findAllRaw();
			for( GlobalSettings globalSettings : settings ) {
				map.put(globalSettings.getKey(), globalSettings.getValue());
			}
			responseDto.setData(map);
			responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
			responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
			return responseDto;
		}
		catch( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage(e.getMessage());
			return responseDto;
		}
	}
}
