package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class OutletReconcileDto {
	@NotEmpty
	@NotNull
	private DepartmentDto outlet;

	@NotEmpty @NotNull
	private DepartmentDto location;

	@NotEmpty @NotNull
	private List< OutletStockItemDto > items;
}
