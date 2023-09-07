package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.modules.emr.model.PatientVisitHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRevisitRepository extends JpaRepository< PatientVisitHistory, Long > {

	Optional< PatientVisitHistory > findTopByOrderByIdDesc();
}
