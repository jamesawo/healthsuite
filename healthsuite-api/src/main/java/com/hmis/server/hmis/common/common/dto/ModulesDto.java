package com.hmis.server.hmis.common.common.dto;

import com.hmis.server.hmis.common.common.model.Permission;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class ModulesDto {
    private String name;
    private List<Permission> permissions;
}
