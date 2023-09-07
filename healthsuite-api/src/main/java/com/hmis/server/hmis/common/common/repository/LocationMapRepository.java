package com.hmis.server.hmis.common.common.repository;

import com.hmis.server.hmis.common.common.model.ApplicationModule;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.model.LocationMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationMapRepository extends JpaRepository<LocationMap, Long> {

    LocationMap findByApplicationModule(ApplicationModule applicationModule);

    Optional<LocationMap> findByDepartmentCategory(DepartmentCategory departmentCategory);
}
