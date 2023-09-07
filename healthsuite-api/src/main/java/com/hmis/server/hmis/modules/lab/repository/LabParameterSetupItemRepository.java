package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabParameter;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetup;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetupItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabParameterSetupItemRepository extends JpaRepository<LabParameterSetupItem, Long> {
    Optional<LabParameterSetupItem> findByLabParameterSetupAndParameter(LabParameterSetup labParameterSetup, LabParameter parameter);
}
