package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientBillRepository extends JpaRepository< PatientBill, Long > {

	List< PatientBill > findAllByInvoiceNumberIsLikeIgnoreCase(String invoiceNumber);

	List< PatientBill > findAllByInvoiceNumberIsLikeIgnoreCaseAndIsPaid(String invoiceNumber, Boolean isPaid);

	List< PatientBill > findAllByPatient(PatientDetail patient);

	List< PatientBill > findAllByPatientAndIsPaid(PatientDetail patient, Boolean isPaid);

	Optional< PatientBill > findByInvoiceNumber(String invoiceNumber);

	@Transactional
	@Modifying( flushAutomatically = true, clearAutomatically = true )
	@Query( "update PatientBill t set t.isPaid = :status, t.receiptNumber = :receiptNumber where t.id = :id" )
	void setBillIsPaid(@Param("status") boolean status, @Param("receiptNumber") String receiptNumber, @Param("id") Long id);

	List<PatientBill> findAllByPatientAdmission(PatientAdmission patientAdmission);
}
