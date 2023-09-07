package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.nurse.model.NurseObPreviousMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseObPreviousMedicalRepository extends JpaRepository<NurseObPreviousMedical, Long> {
}
