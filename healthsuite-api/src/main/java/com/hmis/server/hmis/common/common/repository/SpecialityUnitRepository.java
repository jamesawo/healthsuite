package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialityUnitRepository extends JpaRepository<SpecialityUnit, Long> {

    Optional<SpecialityUnit> findTopByOrderByIdDesc();

    List<SpecialityUnit> findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase(String name, String code);
}
