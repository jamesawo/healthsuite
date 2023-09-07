package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface LabParameterSetupRepository extends JpaRepository<LabParameterSetup,Long> {
    Optional<LabParameterSetup> findByTest(ProductService test);
}
