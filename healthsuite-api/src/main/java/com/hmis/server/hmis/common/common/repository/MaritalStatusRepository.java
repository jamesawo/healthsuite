package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.MaritalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaritalStatusRepository extends JpaRepository<MaritalStatus, Long> {
    MaritalStatus findByName(String name);

    List<MaritalStatus> findByNameIsLikeIgnoreCase(String name);
}
