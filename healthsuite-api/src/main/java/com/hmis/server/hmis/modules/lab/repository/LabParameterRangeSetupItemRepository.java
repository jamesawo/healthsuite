package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabParameterRangeSetup;
import com.hmis.server.hmis.modules.lab.model.LabParameterRangeSetupItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabParameterRangeSetupItemRepository extends JpaRepository<LabParameterRangeSetupItem, Long> {
    Optional<LabParameterRangeSetupItem> findByNameIgnoreCaseAndRangeSetup(String name, LabParameterRangeSetup rangeSetup);
}
