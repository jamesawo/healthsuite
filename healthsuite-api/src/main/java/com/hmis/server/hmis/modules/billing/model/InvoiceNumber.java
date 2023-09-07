package com.hmis.server.hmis.modules.billing.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Entity
@Table( name = "hmis_invoice_number_data")
@NoArgsConstructor
public class InvoiceNumber {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String prefix;

	@Column
	private AtomicInteger number;

}
