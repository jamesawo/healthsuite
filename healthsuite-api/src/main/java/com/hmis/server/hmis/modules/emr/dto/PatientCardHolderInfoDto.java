package com.hmis.server.hmis.modules.emr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.RelationshipDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PatientCardHolderInfoDto {
	private Optional<Long> id;
	private String name;
	private String insuranceNumber;
	private DateDto cardExpiry;
	private CardHolderTypeEnum cardHolderType;
	private String placeOfWork;
	private String department;
	private String beneficiaryName;
	private RelationshipDto relationShipWithCardHolder;
}
