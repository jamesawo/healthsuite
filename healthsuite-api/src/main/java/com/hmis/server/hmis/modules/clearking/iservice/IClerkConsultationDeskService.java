package com.hmis.server.hmis.modules.clearking.iservice;

import com.hmis.server.hmis.modules.clearking.dto.ClerkDoctorRequestDto;

public interface IClerkConsultationDeskService<D, T> {
    void saveDoctorsRequest(ClerkDoctorRequestDto request);
    void saveTemplate(T template);
    void saveSession(D desk);
    void saveSessionAndTemplate(D desk, T template);
}
