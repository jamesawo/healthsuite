package com.hmis.server.hmis.common.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table( name = "hmis_scheme_service_price" )
@NoArgsConstructor
public class SchemeServicePrice {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@OneToOne
	@JoinColumn( name = "service_id", nullable = false )
	private ProductService service;

	@OneToOne
	@JoinColumn( name = "scheme_id", nullable = false )
	private Scheme scheme;

	@Column( name = "price", nullable = false )
	private double price;

}
