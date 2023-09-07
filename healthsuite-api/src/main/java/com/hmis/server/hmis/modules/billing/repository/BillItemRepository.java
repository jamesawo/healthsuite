package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillItemRepository extends JpaRepository< PatientServiceBillItem, Long > {
	List< PatientServiceBillItem > findAllByPatientBill(PatientBill patientBill);
}
