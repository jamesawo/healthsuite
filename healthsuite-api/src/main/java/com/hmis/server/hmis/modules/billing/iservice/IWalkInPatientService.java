package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.modules.billing.dto.WalkInPatientDto;
import com.hmis.server.hmis.modules.billing.model.WalkInPatient;

public interface IWalkInPatientService {


	WalkInPatient findById( Long id );

	WalkInPatient createWalkInPatient( WalkInPatientDto dto );

	WalkInPatient searchWalkInPatient( String searchTerm );
}
