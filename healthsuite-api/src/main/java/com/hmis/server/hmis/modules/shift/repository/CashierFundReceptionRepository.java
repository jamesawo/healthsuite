package com.hmis.server.hmis.modules.shift.repository;

import com.hmis.server.hmis.modules.shift.model.CashierFundReception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashierFundReceptionRepository extends JpaRepository<CashierFundReception, Long> {
    Optional<CashierFundReception> findByShift_Id(Long shift_id);
}
