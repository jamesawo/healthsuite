package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.SchemeDto;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.modules.billing.dto.SchemeBillDto;

import java.util.List;

public interface ISchemeService {
	Scheme createOne( SchemeDto schemeDto );

	List<SchemeDto> createInBatch( List<SchemeDto> schemeDtoList );

	List<Scheme> findAll();

	List<SchemeDto> findAllScheme();

	Scheme findById( Long id );

	List<SchemeDto> findByNameLike( SchemeDto schemeDto );

	SchemeDto findByName( SchemeDto schemeDto );

	SchemeDto findByCode( SchemeDto schemeDto );

	Scheme updateOne( SchemeDto schemeDto );

	List<SchemeDto> updateInBatch( List<SchemeDto> schemeDtoList );

	void deactivateOne( SchemeDto schemeDto );

	void deactivateInBatch( List<SchemeDto> schemeDtoList );

	void activateOne( SchemeDto schemeDto );

	void activateInBatch( List<SchemeDto> schemeDtoList );

	List<SchemeDto> findByInsuranceNameOrCode( String search );

	void addPatientBillToScheme( SchemeBillDto dto );
}
