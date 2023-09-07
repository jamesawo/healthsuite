package com.hmis.server.hmis.modules.others.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table( name = "hmis_vendor_data" )
@NoArgsConstructor @ToString
@EqualsAndHashCode( callSuper=true)
public class Vendor extends Auditable<String> {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column(name = "supplier_name", nullable =  false)
	private String supplierName;

	@Column(name = "phone_number", nullable =  false)
	private String phoneNumber;

	@Column(name = "company_registration")
	private String companyRegistration;

	@Column(name = "office_address", nullable =  false)
	private String officeAddress;

	@Column(name = "postal_address")
	private String postalAddress;

	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "website_address")
	private String websiteUrl;

	@Column(name = "fax_number")
	private String faxNumber;

	@Column(name = "is_pharmacy_vendor")
	private Boolean isPharmacyVendor;
}
