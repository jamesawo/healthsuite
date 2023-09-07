package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import com.hmis.server.hmis.modules.lab.model.LabTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestResultRepository extends JpaRepository<LabTestResult, Long> {
    Optional<LabTestResult> findByPatientAndTestPreparationAndTestRequestItem(PatientDetail patient, LabTestPreparation testPreparation, LabTestRequestItem testRequestItem);

    Optional<LabTestResult> findByTestPreparationAndTestRequestItem(LabTestPreparation testPreparation, LabTestRequestItem testRequestItem);
}
