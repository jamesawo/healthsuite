package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.nurse.model.PatientIcuBounce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientIcuBounceRepository  extends JpaRepository<PatientIcuBounce, Long> {
}
