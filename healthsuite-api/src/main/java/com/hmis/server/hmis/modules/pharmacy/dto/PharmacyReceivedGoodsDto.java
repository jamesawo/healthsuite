package com.hmis.server.hmis.modules.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
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
public class PharmacyReceivedGoodsDto {
	private Long id;
	private DrugOrderDto drugOrder;
	private DepartmentDto receivingDepartment;
	private VendorDto supplyingCompany;
	private String receivedBy;
	private String deliveredBy;
	private String invoiceNumber;
	private DateDto invoiceDate;
	private String purchaseOrderNumber;
	private String deliveryNoteNumber;
	private DepartmentDto outlet;
	private UserDto user;
	private List<PharmacyReceivedGoodsItemDto> receivedGoodsItemsList;
	private String relatedInformation;
	private Double totalAmountSupplied;

}
