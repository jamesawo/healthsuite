package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.ServiceReceiptNumber;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceReceiptNumberRepository extends JpaRepository< ServiceReceiptNumber, Long> {
	Optional< ServiceReceiptNumber> findTopByOrderByIdDesc();
}
