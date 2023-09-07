package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Religion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReligionRepository extends JpaRepository<Religion, Long> {
}
