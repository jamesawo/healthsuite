package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.dto.ClinicDeskEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkingDoctorTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorClerkingTemplateRepository extends JpaRepository<ClerkingDoctorTemplate, Long> {
    Optional<ClerkingDoctorTemplate> findTopByOrderByIdDesc();
    List<ClerkingDoctorTemplate> findAllByTemplateNameContainsIgnoreCaseAndSavedByAndDeskEnum(String templateName, User savedBy, ClinicDeskEnum deskEnum);
}
