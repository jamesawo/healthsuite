package com.hmis.server.hmis.modules.clearking.dto;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;

@Data
public class DoctorListWrapper {
    private User doctor;
    private PatientDetail patient;
    private Department clinic;
}
