package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NationalityDto {
    private Long id;
    private String name;
    private List<String> children;
    private List<String> childrenString;
    private String parent;
    private NationalityEnum type;
    private List<NationalityDto> childrenDto;
    private NationalityDto parentDto;

    public NationalityDto(Long id, String name){
        this.id = id;
        this.name = name;
    }

}
