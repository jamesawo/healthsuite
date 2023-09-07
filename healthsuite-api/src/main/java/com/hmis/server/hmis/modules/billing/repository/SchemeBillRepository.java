package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.modules.billing.model.SchemeBill;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SchemeBillRepository extends JpaRepository<SchemeBill, Long> {
	List<SchemeBill> findAllByDateIsLessThanEqualAndDateGreaterThanEqual( LocalDate endDate, LocalDate startDate );

	List<SchemeBill> findAllBySchemeAndDateIsLessThanEqualAndDateGreaterThanEqual(
			Scheme scheme, LocalDate date, LocalDate date2 );

	List<SchemeBill> findAllByPatientDetailAndDateIsLessThanEqualAndDateGreaterThanEqual(
			PatientDetail patientDetail, LocalDate date, LocalDate date2 );
}
