package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import com.hmis.server.hmis.modules.lab.model.LabTestTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestTrackerRepository extends JpaRepository<LabTestTracker, Long> {
    // Optional<LabTestTracker> findByLabTestCode(String code);

    Optional<LabTestTracker> findByLabTestRequest_Id(Long labBillTestRequest_id);

    Optional<LabTestTracker> findByLabTestRequestItem(LabTestRequestItem labTestRequestItem);

}
