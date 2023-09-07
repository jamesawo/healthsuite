package com.hmis.server.hmis.common.common.model;

import com.hmis.server.hmis.common.common.dto.SchemeDto;
import com.hmis.server.hmis.common.common.dto.SchemeOrganizationTypeEnum;
import com.hmis.server.hmis.common.common.dto.SchemePlanDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.hmis.server.hmis.common.constant.HmisConstant.NIL;

@Data
@Entity
@Table( name = "hmis_scheme" )
@NoArgsConstructor
public class Scheme {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "insurance_name" )
	private String insuranceName;

	@Column( unique = true, name = "insurance_code" )
	private String insuranceCode;

	@Column( name = "phone_number" )
	private String phoneNumber;

	@Column( name = "postal_address" )
	private String postalAddress;

	@Column( name = "email_address" )
	private String emailAddress;

	@Column( name = "address_line_title" )
	private String addressLineTitle;

	@Column( name = "discount" )
	private int discount;

	@Column( name = "address_one" )
	private String address1 = "N/A";

	@Column( name = "address_two" )
	private String address2 = "N/A";

	@Column( name = "address_four" )
	private String address3 = "N/A";

	@Column( name = "address_five" )
	private String address4 = "N/A";


	@Column( name = "organization_type" )
	private SchemeOrganizationTypeEnum organizationTypeEnum = SchemeOrganizationTypeEnum.INDIVIDUAL;

	@OneToMany( mappedBy = "scheme" )
	private List<SchemePlan> planList;

	public Scheme( Long id ) {
		this.id = id;
	}

	public SchemeDto mapToDto() {
		SchemeDto dto = new SchemeDto();
		dto.setId( this.id );
		dto.setInsuranceName( this.insuranceName );
		dto.setInsuranceCode( this.insuranceCode );
		dto.setPhoneNumber( this.phoneNumber );
		dto.setEmailAddress( this.emailAddress );
		dto.setPostalAddress( this.postalAddress );
		dto.setAddressLineTitle( this.addressLineTitle );
		dto.setDiscount( this.discount );
		dto.setAddress1( this.address1 );
		dto.setAddress2( this.address2 );
		dto.setAddress3( this.address3 );
		dto.setAddress4( this.address4 );
		dto.setLabel( this.insuranceName + " [ " + this.insuranceCode + " ]" );
		dto.setOrganizationType( this.organizationTypeEnum != null ? this.organizationTypeEnum.name() : NIL );

		dto.setSchemePlans( this.planList
				                    .stream()
				                    .map( plan -> new SchemePlanDto(
						                    plan.getId(),
						                    plan.getPlanName(),
						                    plan.getDiscountMargin(),
						                    plan.getServicePercent(),
						                    plan.getDrugPercent()
				                    ) ).collect( Collectors.toList() ) );
		return dto;
	}
}
