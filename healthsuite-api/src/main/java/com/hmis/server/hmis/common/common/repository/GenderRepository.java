package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Long> {
    Gender findByName(String name);

    Gender findByNameIsContaining(String name);

}
