package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabParasitologyResultTemplate;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabParasitologyResultTemplateRepository extends JpaRepository<LabParasitologyResultTemplate, Long> {
    Optional<LabParasitologyResultTemplate> findByTestPreparation(LabTestPreparation testPreparation);
}
