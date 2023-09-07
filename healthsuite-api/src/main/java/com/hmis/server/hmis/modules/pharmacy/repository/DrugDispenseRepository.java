package com.hmis.server.hmis.modules.pharmacy.repository;

import com.hmis.server.hmis.modules.billing.model.PaymentReceipt;
import com.hmis.server.hmis.modules.pharmacy.model.DrugDispense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugDispenseRepository extends JpaRepository<DrugDispense, Long> {
    Optional<DrugDispense> findByReceipt(PaymentReceipt receipt);
}
