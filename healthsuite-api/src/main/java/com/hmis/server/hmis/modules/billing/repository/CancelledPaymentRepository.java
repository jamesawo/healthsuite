package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.CancelledPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelledPaymentRepository extends JpaRepository<CancelledPayment, Long> {
}
