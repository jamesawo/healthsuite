package com.hmis.server.hmis.modules.billing.repository;

import com.hmis.server.hmis.modules.billing.model.PatientDepositLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientDepositLogRepository extends JpaRepository< PatientDepositLog, Long > {
    List<PatientDepositLog> findAllByDateIsLessThanEqualAndDateGreaterThanEqual(LocalDate date, LocalDate date2);
}
