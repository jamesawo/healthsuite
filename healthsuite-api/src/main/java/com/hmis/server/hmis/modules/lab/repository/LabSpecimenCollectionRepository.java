package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabSpecimenCollection;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabSpecimenCollectionRepository extends JpaRepository<LabSpecimenCollection, Long> {
    Optional<LabSpecimenCollection> findByLabTestRequest_Id(Long labBillTestRequest_id);

    Optional<LabSpecimenCollection> findByLabTestRequestItem(LabTestRequestItem labTestRequestItem);
}
