package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DrugClassificationDto;
import com.hmis.server.hmis.common.common.dto.DrugFormulationDto;
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
public class DrugRegisterDto {
	private Long id;

	@NotNull( message = "Revenue Department is Required" )
	private Long revenueDepartmentId;

	@NotNull( message = "Drug Formulation is Department is Required" )
	private Long formulationId;

	@NotNull( message = "Drug Classification is Required" )
	private Long classificationId;

	@NotEmpty( message = "Generic Name is Required" )
	private String genericName;

	@NotEmpty( message = "Brand Name is Required" )
	private String brandName;

	private String strength;

	@NotNull( message = "Unit Of Issue is Required" )
	private Integer unitOfIssue;

	private Integer unitPerPack;

	@NotNull( message = "Pack Per Packing Unit is Required" )
	private Integer packsPerPackingUnit;

	@NotNull( message = "Cost Price is Required" )
	private Double costPrice;

	@NotNull( message = "Unit Cost Price is Required" )
	private Double unitCostPrice;

	@NotNull( message = "Nhis Markup is Required" )
	private Integer nhisMarkUp;

	@NotNull( message = "General Markup is Required" )
	private Integer generalMarkUp;

	@NotNull( message = "Regular Selling Price is Required" )
	private Double regularSellingPrice;

	@NotNull( message = "Nhis Selling Price is Required" )
	private Double nhisSellingPrice;

	private Double discountPercent;

	private String searchTitle;

	private String description;

	private String code;

	private int availableQty;

	private DrugClassificationDto classification;

	private DrugFormulationDto formulation;

	private int reorderLevel;

	private int issuingOutletBal;
}
