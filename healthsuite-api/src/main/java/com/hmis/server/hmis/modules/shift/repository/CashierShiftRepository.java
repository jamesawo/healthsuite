package com.hmis.server.hmis.modules.shift.repository;

import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.shift.model.CashierCompiledShift;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashierShiftRepository extends JpaRepository<CashierShift, Long> {
    Optional<CashierShift> findTopByOrderByIdDesc();

    Optional<CashierShift> findByCashierAndIsActiveAndOpenDate(User cashier, Boolean isActive, LocalDate openDate);

    Optional<CashierShift> findByCode(String code);

    List<CashierShift> findAllByCodeIsContainingIgnoreCase(String code);

    List<CashierShift> findAllByOpenDateAndIsActive(LocalDate openDate, Boolean isActive);

    List<CashierShift> findAllByCashierAndOpenDateIsBeforeAndIsActive(User cashier, LocalDate openDate, Boolean isActive);

    List<CashierShift> findAllByCashierAndOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(User cashier, LocalDate endDate, LocalDate startDate);

    List<CashierShift> findAllByOpenDateIsLessThanEqualAndOpenDateIsGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    @Transactional
    @Modifying
    @Query( value = "update CashierShift c set c.isShitCompiled = :isCompiled, c.compiledShift = :compileShift where c.id = :id")
    void updateShiftCompileRecord(@Param("isCompiled") boolean isCompiled, @Param("compileShift") CashierCompiledShift compileShift, @Param("id") Long id );
}
