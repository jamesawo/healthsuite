package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.nurse.model.NurseObHisOfPresentPregnancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseObHisOfPresentPregnancyRepository extends JpaRepository<NurseObHisOfPresentPregnancy, Long> {
}
