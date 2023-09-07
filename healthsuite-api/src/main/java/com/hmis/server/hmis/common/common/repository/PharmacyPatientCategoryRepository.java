package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.PharmacyPatientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyPatientCategoryRepository extends JpaRepository<PharmacyPatientCategory, Long> {
}
