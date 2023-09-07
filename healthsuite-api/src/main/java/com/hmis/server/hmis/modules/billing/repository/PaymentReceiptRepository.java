package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.dto.ReceiptStatusEnum;
import com.hmis.server.hmis.modules.billing.model.PaymentReceipt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PaymentReceiptRepository extends JpaRepository< PaymentReceipt, Long> {
	List< PaymentReceipt > findByPatientDetailId(Long patientId);

	Optional< PaymentReceipt > findByReceiptNumberEquals(String receiptNumber);

	@Transactional
	@Query( value = "update PaymentReceipt r set r.isUsed = :used, r.receiptStatusEnum = :status  where r.id = :id " )
	@Modifying
	void setReceiptIsUsed(@Param( "id" ) Long id, @Param( "used" ) boolean used, @Param( "status" ) ReceiptStatusEnum statusEnum);
}
