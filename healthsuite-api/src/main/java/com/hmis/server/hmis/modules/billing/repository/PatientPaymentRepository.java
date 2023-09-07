package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.common.common.model.PaymentMethod;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientPaymentRepository extends JpaRepository< PatientPayment, Long >, JpaSpecificationExecutor<PatientPayment> {
    List<PatientPayment> findAllByDateIsLessThanEqualAndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    List<PatientPayment> findAllByPaymentMethodAndDateIsLessThanEqualAndDateGreaterThanEqual(PaymentMethod paymentMethod, LocalDate date, LocalDate date2);
}
