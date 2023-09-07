package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabParameterRangeSetupItem;
import com.hmis.server.hmis.modules.lab.model.LabTestResult;
import com.hmis.server.hmis.modules.lab.model.LabTestResultItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestResultItemRepository  extends JpaRepository<LabTestResultItem, Long> {
    Optional<LabTestResultItem> findByLabTestResultAndRangeSetupItem(LabTestResult labTestResult, LabParameterRangeSetupItem rangeSetupItem);
}
