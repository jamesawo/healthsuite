package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.modules.clearking.dto.ConsultationDeskEnum;
import com.hmis.server.hmis.modules.clearking.model.ClerkConsultation;
import com.hmis.server.hmis.modules.clearking.model.ClerkGeneralClerkDesk;
import com.hmis.server.hmis.modules.clearking.model.ClerkingGeneralOutPatientDesk;
import com.hmis.server.hmis.modules.clearking.repository.ClerkConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* after saving clerk session from any desk call this consultation service to save the consultation
* instance later for patient e-folder report */
public class ClerkConsultationServiceImpl {
    private final ClerkConsultationRepository consultationRepository;
    private final PatientClerkActivityService activityService;

    @Autowired
    public ClerkConsultationServiceImpl(
            ClerkConsultationRepository consultationRepository,
            PatientClerkActivityService activityService) {
        this.consultationRepository = consultationRepository;
        this.activityService = activityService;
    }

    public void saveGeneralConsultation(ClerkGeneralClerkDesk generalClerkDesk){
        ClerkConsultation consultation = new ClerkConsultation();
        consultation.setDeskEnum(ConsultationDeskEnum.GENERAL_CLERKING_DESK);
        consultation.setGeneralClerkDesk(generalClerkDesk);
        ClerkConsultation save = this.consultationRepository.save(consultation);
        this.activityService.saveConsultationActivity(save, generalClerkDesk.getPatientDetail());
    }

    public void saveGeneralOutPatientDesk(ClerkingGeneralOutPatientDesk generalOutPatientDesk){
        ClerkConsultation consultation = new ClerkConsultation();
        consultation.setDeskEnum(ConsultationDeskEnum.GENERAL_OUT_PATIENT_DESK);
        consultation.setOutPatientDesk(generalOutPatientDesk);
        ClerkConsultation save = this.consultationRepository.save(consultation);
        this.activityService.saveConsultationActivity(save, generalOutPatientDesk.getPatientDetail());
    }
}