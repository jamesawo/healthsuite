package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyBillItemRepository extends JpaRepository< PharmacyBillItem, Long> {
	List< PharmacyBillItem> findAllByPatientBill(PatientBill patientBill);
}
