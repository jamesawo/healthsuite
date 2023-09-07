package com.hmis.server.hmis.modules.others.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class VendorDto {
	private long id;
	@NotEmpty(message = "Supplier Name is Required")
	private String supplierName;
	@NotEmpty(message = "Supplier Phone is Required")
	private String phoneNumber;
	private String companyRegistration;
	@NotEmpty(message = "Supplier Address is Required")
	private String officeAddress;
	private String postalAddress;
	private String emailAddress;
	private String websiteUrl;
	private String faxNumber;
	private Boolean isPharmacyVendor;
}
