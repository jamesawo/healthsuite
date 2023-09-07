package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.modules.lab.model.LabParameterRangeSetup;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetupItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabParameterRangeSetupRepository extends JpaRepository<LabParameterRangeSetup, Long> {

    Optional<LabParameterRangeSetup> findByTestAndLabParameterSetupItem(ProductService test, LabParameterSetupItem labParameterSetupItem);

}
