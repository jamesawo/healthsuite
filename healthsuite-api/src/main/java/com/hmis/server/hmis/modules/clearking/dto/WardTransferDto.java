package com.hmis.server.hmis.modules.clearking.dto;

import com.hmis.server.hmis.common.common.model.Bed;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.emr.model.PatientAdmission;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import lombok.Data;

@Data
public class WardTransferDto {
    private PatientAdmission admissionBeforeUpdate;
    private PatientDetail patient;
    private Bed newBed;
    private Ward newWard;
    private User user;
    private String note;
    private Department location;
    private boolean hasConsultant;
    private User consultant;

    public WardTransferDto(
            PatientAdmission admissionBeforeUpdate,
            PatientDetail patient,
            Bed newBed,
            Ward newWard,
            User user,
            String note,
            Department location,
            boolean hasConsultant,
            User consultant) {
        this.admissionBeforeUpdate = admissionBeforeUpdate;
        this.patient = patient;
        this.newBed = newBed;
        this.newWard = newWard;
        this.user = user;
        this.note = note;
        this.location = location;
        this.hasConsultant = hasConsultant;
        this.consultant = consultant;
    }
}
