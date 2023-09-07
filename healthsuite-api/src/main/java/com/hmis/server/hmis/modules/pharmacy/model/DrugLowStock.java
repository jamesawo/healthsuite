package com.hmis.server.hmis.modules.pharmacy.model;

import com.hmis.server.hmis.common.common.model.Department;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table( name = "hmis_drug_low_stock_data" )
@NoArgsConstructor
@ToString
public class DrugLowStock {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department outlet;

	@OneToOne
	@JoinColumn(name = "drug_register_id", nullable = false)
	private DrugRegister drug;

	@Column(name = "date_time")
	private LocalDateTime dateTime = LocalDateTime.now();

	@Column( name = "email_alert_count" )
	@ColumnDefault( "0" )
	private Integer emailAlertCount = 0;

	@Column( name = "sms_alert_count" )
	@ColumnDefault( "0" )
	private Integer smsAlertCount = 0;

	@Column( name = "web_alert_count" )
	@ColumnDefault( "0" )
	private Integer webAlertCount = 0;

	public DrugLowStock(Long outletId, Long drugId){
		this.outlet = new Department(outletId);
		this.drug = new DrugRegister(drugId);
	}
}
