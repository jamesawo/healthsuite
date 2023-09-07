package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.PharmacyReceiptNumber;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyReceiptNumberRepository extends JpaRepository< PharmacyReceiptNumber, Long> {
	Optional< PharmacyReceiptNumber> findTopByOrderByIdDesc();
}
