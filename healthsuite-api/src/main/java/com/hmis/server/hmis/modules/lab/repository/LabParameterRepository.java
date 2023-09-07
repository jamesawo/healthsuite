package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabParameterRepository extends JpaRepository<LabParameter, Long> {
    Optional<LabParameter> findByTitle(String title);

    List<LabParameter> findByTitleIsContainingIgnoreCase(String title);
}
