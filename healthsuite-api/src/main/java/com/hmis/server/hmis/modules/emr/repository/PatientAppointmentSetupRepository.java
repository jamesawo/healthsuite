package com.hmis.server.hmis.modules.emr.repository;

import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientAppointmentSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientAppointmentSetupRepository extends JpaRepository< PatientAppointmentSetup, Long > {

	boolean existsByConsultantAndSpecialityUnit( User consultant, SpecialityUnit specialityUnit );

	Optional< PatientAppointmentSetup > findByConsultant( User consultant );


}

