package com.hmis.server.hmis.modules.nurse.repository;

import com.hmis.server.hmis.modules.nurse.model.NurseWaitingList;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseWaitingListRepository extends JpaRepository< NurseWaitingList, Long > {

	Optional< NurseWaitingList > findByPatientDetailId(Long id);

}
