package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.MarkUpClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkUpClassRepository extends JpaRepository<MarkUpClass, Long> {
}
