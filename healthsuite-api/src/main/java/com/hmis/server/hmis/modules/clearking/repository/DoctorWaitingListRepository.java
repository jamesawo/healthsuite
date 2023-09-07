package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.DoctorWaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorWaitingListRepository extends JpaRepository<DoctorWaitingList, Long> {
    List<DoctorWaitingList> findAllByClinic_Id(Long clinicId);

    Optional<DoctorWaitingList> findDoctorWaitingListByPatientDetail_Id(Long patientDetail_id);
}
