package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import com.hmis.server.hmis.modules.settings.model.GlobalSettings;
import java.util.List;
import java.util.Map;

public interface IGlobalSettings {
    GlobalSettingsDto createOne(GlobalSettingsDto globalSettingsDto);

	GlobalSettingsDto createIfNotExist(GlobalSettingsDto globalSettingsDto);

	List< GlobalSettingsDto > createInBatch(List< GlobalSettingsDto > globalSettingsDtoList);

    List<GlobalSettingsDto> findAll();

    List<GlobalSettings> findAllRaw();

    List<GlobalSettingsDto> findBySection(GlobalSettingsDto globalSettingsDto);

    GlobalSettingsDto findOne(GlobalSettingsDto globalSettingsDto);

    GlobalSettingsDto findByKey(GlobalSettingsDto globalSettingsDto);

    GlobalSettingsDto updateOne(GlobalSettingsDto globalSettingsDto);

    List<GlobalSettingsDto> updateInBatch(List<GlobalSettingsDto> globalSettingsDtoList);

    void updateFromMap(GlobalSettingsDto globalSettingsDto);

    String findValueByKey(String key);

    Map<String, String> findValuesByKeyList(List<String> keys);

}
