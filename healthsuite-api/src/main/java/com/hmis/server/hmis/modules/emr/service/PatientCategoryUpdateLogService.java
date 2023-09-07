package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.model.PatientCategoryUpdateLog;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientInsuranceDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientCategoryUpdateLogRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientCategoryUpdateLogService {
	private final PatientCategoryUpdateLogRepository logRepository;

	public PatientCategoryUpdateLogService( PatientCategoryUpdateLogRepository logRepository ) {
		this.logRepository = logRepository;
	}

	public void addUpdateLog(
			PatientDetail patient,
			Long location,
			Long user,
			PatientCategoryEnum prevCategory,
			Long prevInsuranceDetailId ) {
		PatientCategoryUpdateLog log = new PatientCategoryUpdateLog();
		log.setLocation( new Department( location ) );
		log.setPatientDetail( patient );
		log.setPrevCategory( prevCategory );
		if ( prevInsuranceDetailId != null ) {
			log.setPrevInsuranceDetail( new PatientInsuranceDetail( prevInsuranceDetailId ) );
		}
		log.setUpdatedBy( new User( user ) );
		this.logRepository.save( log );
	}
}
