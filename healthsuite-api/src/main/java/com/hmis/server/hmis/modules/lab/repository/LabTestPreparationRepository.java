package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.model.LabTestRequest;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestPreparationRepository  extends JpaRepository<LabTestPreparation, Long> {
    Optional<LabTestPreparation> findByPatientDetailAndLabTestRequest(PatientDetail patientDetail, LabTestRequest labTestRequest);

    Optional<LabTestPreparation> findByLabTestRequestItem(LabTestRequestItem labTestRequestItem);
}
