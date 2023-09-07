package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.ApplicationModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationModuleRepository extends JpaRepository<ApplicationModule, Long> {
    ApplicationModule findByName(String name);

    ApplicationModule findByCode(String code);
}
