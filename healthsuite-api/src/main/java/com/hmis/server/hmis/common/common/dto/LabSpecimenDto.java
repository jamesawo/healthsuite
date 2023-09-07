package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data @NoArgsConstructor @AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabSpecimenDto {
    private Optional<Long> id;

    @NotNull
    @Min(2)
    private Optional<String> name;

    public LabSpecimenDto(Long id, String name) {
        this.id = Optional.ofNullable(id);
        this.name = Optional.ofNullable(name);
    }
}
