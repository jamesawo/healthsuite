package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.DepositReceiptNumber;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositReceiptNumberRepository extends JpaRepository< DepositReceiptNumber, Long> {
	Optional< DepositReceiptNumber> findTopByOrderByIdDesc();
}
