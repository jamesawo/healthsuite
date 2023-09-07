package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientImageRepository extends JpaRepository<PatientImage, Long> {
    PatientImage findByName(String name);

    boolean existsFilesByName(String name);
}
