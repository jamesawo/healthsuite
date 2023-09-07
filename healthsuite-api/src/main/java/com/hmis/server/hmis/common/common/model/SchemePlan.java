package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_scheme_plan_data" )
@NoArgsConstructor
public class SchemePlan {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "discount_margin" )
	private int discountMargin;

	@Column( name = "plan_name" )
	private String planName;

	@Column( name = "service_percent" )
	private double servicePercent;

	@Column( name = "drug_percent" )
	private double drugPercent;

	@ManyToOne
	@JoinColumn( name = "scheme_id", nullable = false )
	private Scheme scheme;

	public SchemePlan( Long id ) {
		this.id = id;
	}
}
