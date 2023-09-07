package com.hmis.server.hmis.modules.clearking.repository;

import com.hmis.server.hmis.modules.clearking.model.DoctorWaitingListHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorWaitingListHistoryRepository extends JpaRepository<DoctorWaitingListHistory, Long> {
    List<DoctorWaitingListHistory> findAllByAttendedByUser_Id(Long id);
}
