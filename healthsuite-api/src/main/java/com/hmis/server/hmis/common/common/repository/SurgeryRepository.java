package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
}
