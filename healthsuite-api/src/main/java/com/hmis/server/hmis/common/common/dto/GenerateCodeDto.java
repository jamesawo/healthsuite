package com.hmis.server.hmis.common.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data @ToString @AllArgsConstructor @NoArgsConstructor
public class GenerateCodeDto{
    private Optional<String> globalSettingKey;
    private String defaultPrefix;
    private Optional<String> lastGeneratedCode;
}
