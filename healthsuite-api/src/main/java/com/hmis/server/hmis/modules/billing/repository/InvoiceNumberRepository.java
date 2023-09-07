package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.InvoiceNumber;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceNumberRepository extends JpaRepository< InvoiceNumber, Long > {
	Optional<InvoiceNumber> findTopByOrderByIdDesc();
}
