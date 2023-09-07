package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import com.hmis.server.hmis.modules.lab.model.LabTestVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestVerificationRepository extends JpaRepository<LabTestVerification, Long> {
    Optional<LabTestVerification> findByLabTestRequestItem(LabTestRequestItem labTestRequestItem);
}
