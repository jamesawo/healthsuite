package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.nurse.dto.WaitingStatusEnum;
import com.hmis.server.hmis.modules.nurse.model.NurseWaitingListHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseWaitingListHistoryRepository extends JpaRepository< NurseWaitingListHistory, Long > {
	List< NurseWaitingListHistory > findAllByAttendedByUserAndClinicAndWaitingStatusEnumAndAttendedDate(User attendedByUser, Department clinic, WaitingStatusEnum waitingStatusEnum,
			LocalDate attendedDate);
}
