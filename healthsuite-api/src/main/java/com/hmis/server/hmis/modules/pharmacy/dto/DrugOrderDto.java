package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.others.dto.VendorDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DrugOrderDto {
	private Long id;
	private String code;
	private DepartmentDto outlet;
	private UserDto user;
	private VendorDto vendor;
	private DrugOrderSupplyCategoryEnum supplyCategory;
	private List< DrugOrderItemDto > drugOrderItems;
	private Integer orderItemsCount;
	private Boolean fulfilled;
	private Boolean isStore;
}
