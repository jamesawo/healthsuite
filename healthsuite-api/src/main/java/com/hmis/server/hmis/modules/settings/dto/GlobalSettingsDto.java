package com.hmis.server.hmis.modules.settings.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.modules.settings.model.GlobalSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalSettingsDto {
    private Optional<Long> id;
    private Optional<String> value;
    private Optional<String> key;
    private Optional<String> section;
    private Optional<List<GlobalSettings>> globalSettings;
    private Map<String, String> map;

    public GlobalSettingsDto(Optional<String> key) {
        this.key = key;
    }
}
