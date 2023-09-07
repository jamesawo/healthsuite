package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Bed;
import com.hmis.server.hmis.common.common.model.Ward;
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
public interface BedRepository extends JpaRepository<Bed, Long> {

	Optional<Bed> findTopByOrderByIdDesc();

	@Transactional
	@Modifying
	@Query("update Bed b set b.isOccupied = :status, b.lastAllocateDate = :date where b.id=:id")
	void updateBedIsOccupiedStatus(@Param("id") Long id, @Param("status")boolean status, @Param("date") LocalDate date);

	List<Bed> findAllByWardAndIsOccupied(Ward ward, Boolean isOccupied);
}
