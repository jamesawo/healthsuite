package com.hmis.server.hmis.modules.emr.model;

import com.hmis.server.hmis.common.common.model.Auditable;
import com.hmis.server.hmis.common.common.model.Relationship;
import com.hmis.server.hmis.modules.emr.dto.CardHolderTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table( name = "hmis_patient_card_holder_info_data" )
@EqualsAndHashCode( callSuper = true )
@NoArgsConstructor
@ToString
public class PatientCardHolderInfo extends Auditable<String> {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column
	private String name;

	@Column
	private String insuranceNumber;

	@Column
	private LocalDate cardExpiry;

	@Column
	private String placeOfWork;

	@Column
	private String department;

	@Column
	private CardHolderTypeEnum cardHolderType;

	@Column
	private String beneficiaryName;

	@OneToOne
	@JoinColumn( name = "relationship_id" )
	private Relationship relationshipWithCardHolder;

	public boolean isDependant() {
		return this.getCardHolderType().equals( CardHolderTypeEnum.DEPENDANT );
	}
}
