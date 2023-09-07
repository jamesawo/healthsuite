package com.hmis.server.hmis.modules.shift.repository;

import com.hmis.server.hmis.modules.shift.model.CashierCompiledShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CashierShiftCompileRepository extends JpaRepository<CashierCompiledShift, Long> {
    Optional<CashierCompiledShift> findTopByOrderByIdDesc();
}
