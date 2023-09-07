package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.PatientDepositSum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientDepositSumRepository extends JpaRepository< PatientDepositSum, Long > {

	PatientDepositSum findPatientDepositSumByPatientDetail(PatientDetail patientDetail);

	Optional< PatientDepositSum > findByPatientDetail(PatientDetail patientDetail);

	boolean existsByPatientDetail(PatientDetail patientDetail);

	@Transactional
	@Query( "UPDATE PatientDepositSum t set t.totalDepositAmount = t.totalDepositAmount + :amount WHERE t.patientDetail = :patientDetail" )
	@Modifying
	void addToPatientDepositSumAmount(Double amount, PatientDetail patientDetail);

	@Transactional
	@Query( "UPDATE PatientDepositSum t set t.totalDepositAmount = t.totalDepositAmount - :amount WHERE t.patientDetail = :patientDetail" )
	@Modifying
	void subtractPatientDepositSumAmount(Double amount, PatientDetail patientDetail);
}
