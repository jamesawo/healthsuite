package com.hmis.server.hmis.modules.lab.repository;

import com.hmis.server.hmis.modules.lab.model.LabTestPathologistVerification;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestPathologistVerificationRepository extends JpaRepository<LabTestPathologistVerification, Long> {

	Optional<LabTestPathologistVerification> findByLabTestRequestItem( LabTestRequestItem labTestRequestItem );
}
