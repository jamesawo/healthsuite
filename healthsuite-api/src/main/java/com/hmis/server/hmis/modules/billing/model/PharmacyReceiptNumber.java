package com.hmis.server.hmis.modules.billing.model;

import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "hmis_pharmacy_receipt_number_data")
@NoArgsConstructor
public class PharmacyReceiptNumber {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "prefix")
	private String prefix;

	@Column
	private AtomicInteger number;
}
