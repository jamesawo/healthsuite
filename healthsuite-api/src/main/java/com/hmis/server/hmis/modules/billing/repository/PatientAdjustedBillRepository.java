package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.PatientAdjustedBill;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface PatientAdjustedBillRepository   extends JpaRepository<PatientAdjustedBill, Long> {
}
