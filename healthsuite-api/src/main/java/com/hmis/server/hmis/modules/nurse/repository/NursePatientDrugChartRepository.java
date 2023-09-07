package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.nurse.model.NursePatientDrugChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NursePatientDrugChartRepository extends JpaRepository<NursePatientDrugChart, Long> {
}
