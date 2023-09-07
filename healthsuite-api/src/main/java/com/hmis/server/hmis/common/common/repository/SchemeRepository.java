package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    List<Scheme> findAllByInsuranceNameContainsIgnoreCaseOrInsuranceCodeContainsIgnoreCase(String insuranceName, String insuranceCode);
}
