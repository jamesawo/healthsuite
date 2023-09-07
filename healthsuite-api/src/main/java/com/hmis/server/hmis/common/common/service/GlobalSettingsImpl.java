package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IGlobalSettings;
import com.hmis.server.hmis.common.constant.HmisConfigConstants;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.common.socket.SockDto;
import com.hmis.server.hmis.common.socket.SockMessageService;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import com.hmis.server.hmis.modules.settings.model.GlobalSettings;
import com.hmis.server.hmis.modules.settings.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.ENABLE_NHIS_SERVICE_PRICE;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.TRUE;

@Service
public class GlobalSettingsImpl implements IGlobalSettings {
	private final GlobalSettingsRepository globalSettingsRepository;
	private final SockMessageService sockMessageService;

	@Autowired
	public GlobalSettingsImpl(
			GlobalSettingsRepository globalSettingsRepository,
			@Lazy SockMessageService sockMessageService ) {
		this.globalSettingsRepository = globalSettingsRepository;
		this.sockMessageService = sockMessageService;
	}

	@Override
	public GlobalSettingsDto createOne( GlobalSettingsDto globalSettingsDto ) {
		GlobalSettings globalSettings = globalSettingsRepository.save( mapFromDtoToModel( globalSettingsDto ) );
		return mapFromModelToDto( globalSettings );
	}

	@Override
	public GlobalSettingsDto createIfNotExist( GlobalSettingsDto globalSettingsDto ) {
		GlobalSettings key = this.globalSettingsRepository.findByKey( globalSettingsDto.getKey().get() );
		if ( key != null ) {
			GlobalSettings globalSettings = this.globalSettingsRepository.save(
					this.mapFromDtoToModel( globalSettingsDto ) );
			globalSettingsDto.setId( Optional.of( globalSettings.getId() ) );
			return globalSettingsDto;
		}
		return globalSettingsDto;
	}

	@Override
	public List<GlobalSettingsDto> createInBatch( List<GlobalSettingsDto> globalSettingsDtoList ) {
		return mapModelListToDtoList(
				globalSettingsRepository.saveAll( mapDtoListToModelList( globalSettingsDtoList ) ) );
	}

	@Override
	public List<GlobalSettingsDto> findAll() {
		return mapModelListToDtoList( globalSettingsRepository.findAll() );
	}

	@Override
	public List<GlobalSettings> findAllRaw() {
		return globalSettingsRepository.findAll();
	}

	@Override
	public List<GlobalSettingsDto> findBySection( GlobalSettingsDto globalSettingsDto ) {
		if ( globalSettingsDto.getSection().isPresent() ) {
			return mapModelListToDtoList(
					globalSettingsRepository.findBySection( globalSettingsDto.getSection().get() ) );
		}
		else {
			throw new HmisApplicationException( "Section is Null" );
		}
	}

	@Override
	public GlobalSettingsDto findOne( GlobalSettingsDto globalSettingsDto ) {
		GlobalSettings globalSettings = mapFromDtoToModel( globalSettingsDto );
		return mapFromModelToDto( globalSettingsRepository.getOne( globalSettings.getId() ) );

	}

	@Override
	public GlobalSettingsDto findByKey( GlobalSettingsDto globalSettingsDto ) {
		if ( globalSettingsDto.getKey().isPresent() ) {
			GlobalSettings settings = globalSettingsRepository.findByKey( globalSettingsDto.getKey().get() );
			if ( settings != null ) {
				return mapFromModelToDto( settings );
			}
			else {
				return null; //todo::remove null return to Optional.empty() GlobalSettingDto
			}
		}
		else {
			throw new HmisApplicationException( "Global Setting Key is Required." );
		}
	}

	@Override
	public GlobalSettingsDto updateOne( GlobalSettingsDto globalSettingsDto ) {
		GlobalSettings globalSettings = globalSettingsRepository.save( mapFromDtoToModel( globalSettingsDto ) );
		return mapFromModelToDto( globalSettings );
	}

	@Override
	public List<GlobalSettingsDto> updateInBatch( List<GlobalSettingsDto> globalSettingsDtoList ) {
		List<GlobalSettings> globalSettings = mapDtoListToModelList( globalSettingsDtoList );
		for ( GlobalSettings globalSetting : globalSettings ) {
			globalSettingsRepository.updateGlobalSetting( globalSetting.getKey(), globalSetting.getValue() );
		}
		return globalSettingsDtoList;
	}

	@Override
	public void updateFromMap( GlobalSettingsDto globalSettingsDto ) {
		if ( !globalSettingsDto.getMap().isEmpty() ) {
			for ( Map.Entry<String, String> map : globalSettingsDto.getMap().entrySet() ) {
				globalSettingsRepository.updateGlobalSetting( map.getKey(), map.getValue() );
			}
			//after updating global setting,
			//send message to client soc to pull updated global setting key/value
			this.sockMessageService.sendMessage( HmisConfigConstants.DESTINATION_GLOBAL_SETTING_UPDATED,
			                                     new SockDto( HmisConfigConstants.CONTENT_GLOBAL_SETTING_UPDATED )
			);
			//todo:: send soc message when any seed data is updated
		}
	}

	@Override
	public String findValueByKey( String key ) {
		// only use HmisGlobalSettingKeys as key
		return globalSettingsRepository.findByKey( key ).getValue();
	}

	@Override
	public Map<String, String> findValuesByKeyList( List<String> keys ) {
		if ( keys.isEmpty() ) {
			throw new HmisApplicationException( "Keys cannot be empty" );
		}
		Map<String, String> valuesMap = new HashMap<>();
		for ( String key : keys ) {
			GlobalSettings settings = this.globalSettingsRepository.findByKey( key );
			if ( settings != null ) {
				valuesMap.put( key, settings.getValue() );
			}
		}
		return valuesMap;
	}

	public boolean isNhisServicePriceEnabled() {
		return this.findValueByKey( ENABLE_NHIS_SERVICE_PRICE ).equals( TRUE );

	}

	private GlobalSettings mapFromDtoToModel( GlobalSettingsDto globalSettingsDto ) {
		GlobalSettings globalSettings = new GlobalSettings();
		if ( globalSettingsDto.getId().isPresent() ) {
			globalSettings.setId( globalSettingsDto.getId().get() );
		}
		if ( globalSettingsDto.getKey().isPresent() ) {
			globalSettings.setKey( globalSettingsDto.getKey().get() );
		}
		if ( globalSettingsDto.getValue().isPresent() ) {
			globalSettings.setValue( globalSettingsDto.getValue().get() );
		}
		if ( globalSettingsDto.getSection().isPresent() ) {
			globalSettings.setSection( globalSettingsDto.getSection().get() );
		}
		return globalSettings;
	}

	private GlobalSettingsDto mapFromModelToDto( GlobalSettings globalSettings ) {
		//todo:: refactor this entire service
		if ( globalSettings != null ) {
			GlobalSettingsDto globalSettingsDto = new GlobalSettingsDto();
			globalSettingsDto.setValue( java.util.Optional.ofNullable( globalSettings.getValue() ) );
			globalSettingsDto.setKey( Optional.ofNullable( globalSettings.getKey() ) );
			globalSettingsDto.setId( Optional.ofNullable( globalSettings.getId() ) );
			globalSettingsDto.setSection( Optional.ofNullable( globalSettings.getSection() ) );
			return globalSettingsDto;
		}
		else {
			throw new HmisApplicationException( "Cannot Map From Global Settings Model To Dto: Null" );
		}
	}

	private List<GlobalSettingsDto> mapModelListToDtoList( List<GlobalSettings> globalSettings ) {
		List<GlobalSettingsDto> globalSettingsDtoList = new ArrayList<>();
		if ( globalSettings != null ) {
			globalSettings.forEach( globalSetting -> {
				GlobalSettingsDto globalSettingsDto = new GlobalSettingsDto();
				globalSettingsDto.setId( Optional.ofNullable( globalSetting.getId() ) );
				globalSettingsDto.setKey( Optional.ofNullable( globalSetting.getKey() ) );
				globalSettingsDto.setValue( Optional.ofNullable( globalSetting.getValue() ) );
				globalSettingsDto.setSection( Optional.ofNullable( globalSetting.getSection() ) );
				globalSettingsDtoList.add( globalSettingsDto );
			} );
		}
		return globalSettingsDtoList;
	}

	private List<GlobalSettings> mapDtoListToModelList( List<GlobalSettingsDto> globalSettingsDtoList ) {
		List<GlobalSettings> globalSettings = new ArrayList<>();
		if ( globalSettingsDtoList != null ) {
			for ( GlobalSettingsDto settingsDto : globalSettingsDtoList ) {
				GlobalSettings globalSetting = new GlobalSettings();
				if ( settingsDto.getId().isPresent() ) {
					globalSetting.setId( settingsDto.getId().get() );
				}
				if ( settingsDto.getKey().isPresent() ) {
					globalSetting.setKey( settingsDto.getKey().get() );
				}
				if ( settingsDto.getValue().isPresent() ) {
					globalSetting.setValue( settingsDto.getValue().get() );
				}
				globalSettings.add( globalSetting );
			}
		}
		return globalSettings;
	}
}
